package vesnell.pl.quiz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import vesnell.pl.quiz.database.controller.QuestionController;
import vesnell.pl.quiz.database.model.Quiz;

/**
 * Created by ascen on 2016-04-21.
 */
public class QuestionsActivity extends AppCompatActivity implements DownloadResultReceiver.Receiver {

    private static final String TAG = "QuestionsActivity";

    private DownloadResultReceiver mReceiver;
    private ProgressDialog progressDialog;
    private QuestionController questionController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        Quiz quiz = (Quiz) b.getSerializable(Quiz.NAME);
        String quizId = quiz.getId();
        int questionsCount = quiz.getQuestionsCount();

        final String url = getResources().getString(R.string.quiz_questions_url, quizId);

        setContentView(R.layout.activity_questions);

        questionController = new QuestionController(getApplicationContext());
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

    }
}
