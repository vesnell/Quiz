package vesnell.pl.quiz;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import vesnell.pl.quiz.database.model.Quiz;

/**
 * Created by ascen on 2016-04-21.
 */
public class QuestionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        String quizId = b.getString(Quiz.QUIZ_ID);
        int questionsCount = b.getInt(Quiz.QUESTIONS_COUNT);

        setContentView(R.layout.activity_questions);
    }
}
