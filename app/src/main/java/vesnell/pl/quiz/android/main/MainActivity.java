package vesnell.pl.quiz.android.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import vesnell.pl.quiz.android.questions.QuestionsActivity;
import vesnell.pl.quiz.R;
import vesnell.pl.quiz.android.service.DownloadQuizService;
import vesnell.pl.quiz.android.service.DownloadResultReceiver;
import vesnell.pl.quiz.database.controller.QuizController;
import vesnell.pl.quiz.database.model.Quiz;

public class MainActivity extends AppCompatActivity implements DownloadResultReceiver.Receiver,
        QuizController.QuizzesListSaveCallback {

    private static final String TAG = "MainActivity";
    private static final int REQ_QUESTIONS = 1;

    private static final int ZERO = 0;
    private static final int HUNDRED = 100;

    private ListView listView;
    private ListViewAdapter adapter;
    private DownloadResultReceiver mReceiver;
    private ProgressDialog progressDialog;
    private QuizController quizController;
    private List<Quiz> quizzes;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int nextHundred;
    private boolean userScrolled = false;
    private RunServiceType runServiceType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String startUrl = getResources().getString(R.string.quiz_url, ZERO, HUNDRED);

        setContentView(R.layout.activity_main);

        nextHundred = HUNDRED;
        runServiceType = RunServiceType.FIRST_RUN;

        progressDialog = new ProgressDialog(this);
        quizController = new QuizController(getApplicationContext());
        quizController.setQuizzesListSaveCallback(this);
        listView = (ListView) findViewById(R.id.listView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        adapter = new ListViewAdapter(MainActivity.this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (quizzes != null) {
                    Quiz quiz = quizzes.get(position);
                    Intent i = new Intent(MainActivity.this, QuestionsActivity.class);

                    //send to questions activity quizId and number of questions
                    i.putExtra(Quiz.NAME, quiz);
                    startActivityForResult(i, REQ_QUESTIONS);
                }
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    userScrolled = true;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final int lastItem = firstVisibleItem + visibleItemCount;
                if (userScrolled && lastItem > 0 && lastItem == totalItemCount) {
                    userScrolled = false;
                    //if end of list, load more data
                    runServiceType = RunServiceType.EXPAND;
                    nextHundred += HUNDRED;
                    String url = getResources().getString(R.string.quiz_url, nextHundred, HUNDRED);
                    startDownloadService(url);
                }
            }
        });

        mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(this);

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        runServiceType = RunServiceType.REFRESH;
                        swipeRefreshLayout.setRefreshing(true);
                        startDownloadService(startUrl);
                    }
                }
        );

        //start service to download quizzes
        startDownloadService(startUrl);
    }

    private void startDownloadService(String url) {
        //send extras to download service
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, DownloadQuizService.class);
        intent.putExtra(DownloadQuizService.URL, url);
        intent.putExtra(DownloadQuizService.RECEIVER, mReceiver);
        intent.putExtra(DownloadQuizService.DOWNLOAD_TYPE, DownloadQuizService.DownloadType.QUIZ);
        startService(intent);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case DownloadQuizService.STATUS_RUNNING:
                setEnabledDownloadAction(true);
                break;
            case DownloadQuizService.STATUS_ERROR:
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            case DownloadQuizService.STATUS_FINISHED:
                List<Quiz> quizzes = (List<Quiz>) resultData.getSerializable(DownloadQuizService.RESULT);
                if (quizzes != null && quizzes.size() > 0) {
                    quizController.saveQuizzesList(quizzes);
                } else {
                    showQuizList();
                }
                setEnabledDownloadAction(false);
                break;
        }
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
                adapter.setQuizzes(quizzes);
                listView.invalidateViews();

                if (runServiceType == RunServiceType.REFRESH) {
                    listView.setSelection(0);
                }
            }
        });
        quizController.requestList();
    }

    private void setEnabledDownloadAction(boolean isEnabled) {
        if (isEnabled) {
            if (!swipeRefreshLayout.isRefreshing()) {
                progressDialog.show();
            }
        } else {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            } else {
                progressDialog.cancel();
            }
        }
    }

    public enum RunServiceType {
        FIRST_RUN,
        REFRESH,
        EXPAND,
        BACK_FROM_QUESTIONS;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_QUESTIONS && resultCode == RESULT_OK) {
            runServiceType = RunServiceType.BACK_FROM_QUESTIONS;
            showQuizList();
        }
    }
}
