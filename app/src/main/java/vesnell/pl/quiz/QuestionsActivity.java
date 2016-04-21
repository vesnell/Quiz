package vesnell.pl.quiz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import vesnell.pl.quiz.database.controller.AnswerController;
import vesnell.pl.quiz.database.controller.QuestionController;
import vesnell.pl.quiz.database.model.Answer;
import vesnell.pl.quiz.database.model.Question;
import vesnell.pl.quiz.database.model.Quiz;

/**
 * Created by ascen on 2016-04-21.
 */
public class QuestionsActivity extends AppCompatActivity implements DownloadResultReceiver.Receiver,
        QuestionController.QuestionsListSaveCallback {

    private static final String TAG = "QuestionsActivity";

    private DownloadResultReceiver mReceiver;
    private ProgressDialog progressDialog;
    private QuestionController questionController;
    private AnswerController answerController;
    private Quiz quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        quiz = (Quiz) b.getSerializable(Quiz.NAME);
        String quizId = quiz.getId();
        int questionsCount = quiz.getQuestionsCount();

        final String url = getResources().getString(R.string.quiz_questions_url, quizId);

        setContentView(R.layout.activity_questions);

        questionController = new QuestionController(getApplicationContext());
        answerController = new AnswerController(getApplicationContext());
        progressDialog = new ProgressDialog(this);

        mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(this);

        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, DownloadQuizService.class);

        //send extras to download service
        intent.putExtra(DownloadQuizService.URL, url);
        intent.putExtra(DownloadQuizService.RECEIVER, mReceiver);
        intent.putExtra(DownloadQuizService.DOWNLOAD_TYPE, DownloadType.QUESTION);
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
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            case DownloadQuizService.STATUS_FINISHED:
                List<Question> questions = (List<Question>) resultData.getSerializable(DownloadQuizService.RESULT);
                saveQuestions(questions);
                progressDialog.cancel();
                break;

        }
    }

    private void saveQuestions(List<Question> questions) {
        questionController.setQuestionsListSaveCallback(this);
        questionController.saveQuestionsList(questions, quiz);
    }

    @Override
    public void onQuestionsListSaved(boolean result, List<Question> questions) {
        if (result) {
            loadQuestions();
        } else {
            Log.w(TAG, "error: write to db");
            Toast.makeText(this, R.string.error_write_to_db, Toast.LENGTH_LONG).show();
        }
    }

    private void loadQuestions() {
        questionController.setQuestionsListLoadCallback(new QuestionController.QuestionsListLoadCallback() {
            @Override
            public void onQuestionsListLoaded(List<Question> questions) {
                loadAnswers(questions);
            }
        });
        questionController.requestList(quiz);
    }

    private void loadAnswers(List<Question> questions) {
        for (final Question question : questions) {
            answerController.setAnswersListLoadCallback(new AnswerController.AnswersListLoadCallback() {
                @Override
                public void onAnswersListLoaded(List<Answer> answers) {
                    for (Answer answer : answers) {
                        Log.d(TAG, answer.getQuestion().getText());
                        Log.d(TAG, answer.getText());
                    }
                }
            });
            answerController.requestList(question);
        }
    }

}
