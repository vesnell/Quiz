package vesnell.pl.quiz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import vesnell.pl.quiz.database.controller.QuizController;
import vesnell.pl.quiz.database.model.Quiz;

public class MainActivity extends AppCompatActivity implements DownloadResultReceiver.Receiver,
        QuizController.QuizzesListSaveCallback {

    private static final String TAG = "MainActivity";
    private static final int REQ_QUESTIONS = 1;

    private ListView listView;
    private ListViewAdapter adapter;
    private DownloadResultReceiver mReceiver;
    private ProgressDialog progressDialog;
    private QuizController quizController;
    private List<Quiz> quizzes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String url = getResources().getString(R.string.quiz_url);

        setContentView(R.layout.activity_main);

        quizController = new QuizController(getApplicationContext());
        listView = (ListView) findViewById(R.id.listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (quizzes != null) {
                    String quizId = quizzes.get(position).getId();
                    int questionsCount = quizzes.get(position).getQuestionsCount();
                    Intent i = new Intent(MainActivity.this, QuestionsActivity.class);
                    i.putExtra(Quiz.QUIZ_ID, quizId);
                    i.putExtra(Quiz.QUESTIONS_COUNT, questionsCount);
                    startActivityForResult(i, REQ_QUESTIONS);
                }
            }
        });

        progressDialog = new ProgressDialog(this);

        //start service to download quizzes
        mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, DownloadQuizService.class);

        //send extras to download service
        intent.putExtra(DownloadQuizService.URL, url);
        intent.putExtra(DownloadQuizService.RECEIVER, mReceiver);

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
                List<Quiz> quizzes = (List<Quiz>) resultData.getSerializable(DownloadQuizService.RESULT);
                if (quizzes != null && quizzes.size() > 0) {
                    saveQuiz(quizzes);
                } else {
                    showQuizList();
                }
                progressDialog.cancel();
                break;

        }
    }

    private void saveQuiz(final List<Quiz> quizzes) {
        quizController.setQuizzesListSaveCallback(this);
        quizController.saveQuizzesList(quizzes);
    }

    @Override
    public void onQuizzesListSaved(boolean result) {
        if (result) {
            showQuizList();
        } else {
            Log.w(TAG, "error: write to db");
            Toast.makeText(this, R.string.error_write_to_db, Toast.LENGTH_LONG).show();
        }
    }

    private void showQuizList() {
        quizController.setQuizzesListLoadCallback(new QuizController.QuizzesListLoadCallback() {
            @Override
            public void onQuizzesListLoaded(List<Quiz> quizzes) {
                MainActivity.this.quizzes = quizzes;
                adapter = new ListViewAdapter(MainActivity.this, quizzes);
                listView.setAdapter(adapter);
            }
        });
        quizController.requestList();
    }
}
