package vesnell.pl.quiz.android.questions;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.viewpagerindicator.LinePageIndicator;

import java.util.List;

import vesnell.pl.quiz.R;
import vesnell.pl.quiz.android.service.DownloadQuizService;
import vesnell.pl.quiz.android.service.DownloadResultReceiver;
import vesnell.pl.quiz.database.controller.QuestionController;
import vesnell.pl.quiz.database.controller.QuizController;
import vesnell.pl.quiz.database.model.Question;
import vesnell.pl.quiz.database.model.Quiz;

/**
 * Created by ascen on 2016-04-21.
 */
public class QuestionsActivity extends AppCompatActivity implements DownloadResultReceiver.Receiver,
        QuestionController.QuestionsListSaveCallback,
        QuestionFragment.OnChooseAnswerListener,
        QuizController.QuizSaveCallback {

    private static final String TAG = "QuestionsActivity";
    private static final int QUESTION_ANSWER_DELAY = 500;
    private static final int REQ_RESULTS = 1;

    private DownloadResultReceiver mReceiver;
    private ProgressDialog progressDialog;
    private QuestionController questionController;
    private Quiz quiz;
    private ViewPager viewPager;
    private LinePageIndicator linePageIndicator;
    private QuizController quizController;
    private Handler handler = new Handler();
    private Runnable vpRunnable = new Runnable() {
        @Override
        public void run() {
            int currentItem = viewPager.getCurrentItem();
            viewPager.setCurrentItem(currentItem + 1);
        }
    };
    private boolean isUserClickOnBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        quiz = (Quiz) b.getSerializable(Quiz.NAME);
        String quizId = quiz.getId();

        final String url = getResources().getString(R.string.quiz_questions_url, quizId);

        setContentView(R.layout.activity_questions);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        linePageIndicator = (LinePageIndicator) findViewById(R.id.indicator);

        quizController = new QuizController(getApplicationContext());
        questionController = new QuestionController(getApplicationContext());
        quizController.setQuizSaveCallback(this);
        questionController.setQuestionsListSaveCallback(this);
        progressDialog = new ProgressDialog(this);

        mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(this);

        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, DownloadQuizService.class);

        //send extras to download service
        intent.putExtra(DownloadQuizService.URL, url);
        intent.putExtra(DownloadQuizService.RECEIVER, mReceiver);
        intent.putExtra(DownloadQuizService.DOWNLOAD_TYPE, DownloadQuizService.DownloadType.QUESTION);
        intent.putExtra(Quiz.NAME, quiz);
        startService(intent);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case DownloadQuizService.STATUS_RUNNING:
                progressDialog.show();
                break;
            case DownloadQuizService.STATUS_ERROR:
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Log.w(TAG, error);
            case DownloadQuizService.STATUS_FINISHED:
                List<Question> questions = (List<Question>) resultData.getSerializable(DownloadQuizService.RESULT);
                if (questions != null && questions.size() > 0) {
                    questionController.saveQuestionsList(questions, quiz);
                } else {
                    showQuestions();
                }
                progressDialog.cancel();
                break;

        }
    }

    private void showQuestions() {
        QuizController quizController = new QuizController(this);
        quizController.setQuizLoadCallback(new QuizController.QuizLoadCallback() {
            @Override
            public void onQuizLoaded(Quiz quiz) {
                if (quiz.getQuestions() != null && quiz.getQuestions().size() > 0) {
                    QuestionPagerAdapter questionPagerAdapter = new QuestionPagerAdapter(getSupportFragmentManager(), quiz);
                    viewPager.setAdapter(questionPagerAdapter);
                    viewPager.setCurrentItem(quiz.getState());
                    linePageIndicator.setViewPager(viewPager);
                    linePageIndicator.setCurrentItem(quiz.getState());
                } else {
                    Toast.makeText(getApplicationContext(), R.string.empty_questions, Toast.LENGTH_LONG).show();
                    onBackPressed();
                }
            }
        });
        quizController.loadQuiz(quiz.getId());
    }

    @Override
    public void onQuestionsListSaved(boolean result, List<Question> questions) {
        if (result) {
            showQuestions();
        } else {
            Log.w(TAG, "error: write to db");
            Toast.makeText(this, R.string.error_write_to_db, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void setNextQuestion(boolean isCorrectAnswer) {
        if (isCorrectAnswer) {
            quiz.setCorrectAnswers(quiz.getCorrectAnswers() + 1);
        }
        updateQuiz();
    }

    @Override
    public void onBackPressed() {
        isUserClickOnBack = true;
        updateQuiz();
    }

    private void updateQuiz() {
        int currentQuestion = viewPager.getCurrentItem();
        quiz.setState(currentQuestion);
        quizController.updateQuiz(quiz);
    }

    @Override
    public void onQuizSaved(boolean result, Quiz quiz) {
        if (result) {
            if (isUserClickOnBack) {
                backToMain();
            } else {
                int currentItem = viewPager.getCurrentItem();
                int totalItems = viewPager.getAdapter().getCount();
                if (currentItem + 1 == totalItems) {
                    handler.removeCallbacks(vpRunnable);
                    openResult(quiz);
                } else {
                    handler.postDelayed(vpRunnable, QUESTION_ANSWER_DELAY);
                }
            }
        } else {
            Log.e(TAG, "quiz not updated");
        }
    }

    private void backToMain() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    private void openResult(Quiz quiz) {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(Quiz.NAME, quiz);
        startActivityForResult(intent, REQ_RESULTS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_RESULTS && resultCode == RESULT_OK) {
            backToMain();
        }
    }
}
