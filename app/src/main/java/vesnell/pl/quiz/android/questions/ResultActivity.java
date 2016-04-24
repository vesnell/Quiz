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
public class ResultActivity extends AppCompatActivity implements QuizController.QuizSaveCallback, View.OnClickListener {

    public static final String RESULT_FINISH_TYPE = "ResultFinishType";
    private static final String TAG = "ResultActivity";

    private Quiz quiz;
    private QuizController quizController;
    private FinishQuizType finishQuizType;

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
        btnBack.setOnClickListener(this);
        btnSolveAgain.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBackToMainScreen:
                finishQuizType = FinishQuizType.BACK_TO_MAIN;
                break;
            case R.id.btnSolveAgain:
                finishQuizType = FinishQuizType.SOLVE_AGAIN;
                break;
        }
        updateQuiz();
    }

    private void updateQuiz() {
        quiz.setState(0);
        quizController.updateQuiz(quiz);
    }

    @Override
    public void onBackPressed() {
        finishQuizType = FinishQuizType.BACK_TO_MAIN;
        updateQuiz();
    }

    @Override
    public void onQuizSaved(boolean result, Quiz quiz) {
        if (!result) {
            Log.e(TAG, "cannot update Quiz table");
        }
        Intent intent = new Intent();
        intent.putExtra(RESULT_FINISH_TYPE, finishQuizType);
        intent.putExtra(Quiz.NAME, quiz);
        setResult(RESULT_OK, intent);
        finish();
    }

    public enum FinishQuizType {
        BACK_TO_MAIN,
        SOLVE_AGAIN;
    }
}
