package vesnell.pl.quiz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vesnell.pl.quiz.database.model.Quiz;

/**
 * Created by ascen on 2016-04-22.
 */
public class QuestionFragment extends Fragment {

    private static final String QUESTION_NR = "questionNr";

    public static QuestionFragment newInstance(int questionNr, Quiz quiz) {
        QuestionFragment questionFragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putInt(QUESTION_NR, questionNr);
        args.putSerializable(Quiz.NAME, quiz);
        questionFragment.setArguments(args);
        return questionFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_question, container, false);
        Quiz quiz = (Quiz) getArguments().getSerializable(Quiz.NAME);
        int qustionNr = getArguments().getInt(QUESTION_NR);
        return v;
    }

}
