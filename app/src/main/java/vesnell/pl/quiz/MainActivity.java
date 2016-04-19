package vesnell.pl.quiz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import vesnell.pl.quiz.model.Quiz;

public class MainActivity extends AppCompatActivity implements DownloadResultReceiver.Receiver {

    private ListView listView;
    private ListViewAdapter adapter;
    private DownloadResultReceiver mReceiver;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String url = getResources().getString(R.string.quiz_url);

        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);

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
            case DownloadQuizService.STATUS_FINISHED:
                progressDialog.cancel();

                List<Quiz> quizzes = (List<Quiz>) resultData.getSerializable(DownloadQuizService.RESULT);

                //update listview
                adapter = new ListViewAdapter(this, quizzes);
                listView.setAdapter(adapter);
                break;
            case DownloadQuizService.STATUS_ERROR:
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                break;
        }
    }
}
