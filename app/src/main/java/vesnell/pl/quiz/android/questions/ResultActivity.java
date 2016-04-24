package vesnell.pl.quiz.android.questions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;

import vesnell.pl.quiz.R;
import vesnell.pl.quiz.database.controller.QuizController;
import vesnell.pl.quiz.database.model.Quiz;


/**
 * Created by ascen on 2016-04-24.
 */
public class ResultActivity extends AppCompatActivity implements QuizController.QuizSaveCallback {

    private static final String TAG = "ResultActivity";

    private Quiz quiz;
    private QuizController quizController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Bundle b = getIntent().getExtras();
        quiz = (Quiz) b.getSerializable(Quiz.NAME);
        quiz.setLastScore(quiz.getCorrectAnswers());
        quiz.setCorrectAnswers(0);

        quizController = new QuizController(getApplicationContext());
        quizController.setQuizSaveCallback(this);
        TextView tvResult = (TextView) findViewById(R.id.tvResult);
        Button btnBack = (Button) findViewById(R.id.btnBackToMainScreen);
        Button btnSolveAgain = (Button) findViewById(R.id.btnSolveAgain);

        tvResult.setText(getString(R.string.final_result, quiz.getScore()));
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        btnSolveAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void back() {
        quiz.setState(0);
        quizController.updateQuiz(quiz);
    }

    @Override
    public void onBackPressed() {
        back();
    }

    @Override
    public void onQuizSaved(boolean result, Quiz quiz) {
        if (!result) {
            Log.e(TAG, "cannot update Quiz table");
        }
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}
