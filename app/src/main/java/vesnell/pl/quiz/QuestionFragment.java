package vesnell.pl.quiz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import vesnell.pl.quiz.database.model.Question;
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
        int questionNr = getArguments().getInt(QUESTION_NR);

        TextView tvQuestionText = (TextView) v.findViewById(R.id.questionText);
        ImageView ivQuestionImage = (ImageView) v.findViewById(R.id.questionImage);

        String imageUrl = null;

        List<Question> questions = quiz.getQuestions();
        for (Question question : questions) {
            if (question.getOrder() == questionNr) {
                tvQuestionText.setText(question.getText());
                imageUrl = question.getImage();
                break;
            }
        }

        if (imageUrl != null && imageUrl.length() > 0) {
            ivQuestionImage.setVisibility(View.VISIBLE);
            Picasso.with(getContext()).load(imageUrl)
                    .resizeDimen(R.dimen.list_item_width, R.dimen.list_item_height).centerCrop().into(ivQuestionImage);
        }
        return v;
    }

}
