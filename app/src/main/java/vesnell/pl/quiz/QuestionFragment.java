package vesnell.pl.quiz;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import vesnell.pl.quiz.database.model.Answer;
import vesnell.pl.quiz.database.model.Question;
import vesnell.pl.quiz.database.model.Quiz;

/**
 * Created by ascen on 2016-04-22.
 */
public class QuestionFragment extends Fragment {

    private static final String QUESTION_NR = "questionNr";
    private OnChooseAnswerListener listener;

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
        FrameLayout flAnswers = (FrameLayout) v.findViewById(R.id.answers_view);

        String imageUrl = null;

        List<Question> questions = quiz.getQuestions();   //zrobic pobieranie pytan juz posortowanie po order
        for (Question question : questions) {
            if (question.getOrder() == questionNr) {
                tvQuestionText.setText(question.getText());
                imageUrl = question.getImage();
                createAnswers(flAnswers, question);
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

    private void createAnswers(FrameLayout flAnswers, final Question question) {
        final RadioButton[] rb = new RadioButton[question.getAnswersCount()];
        RadioGroup rg = new RadioGroup(getContext());
        rg.setOrientation(RadioGroup.VERTICAL);
        List<Answer> answers = question.getAnswers();   //zrobic posortowana list odpowiedzi po order
        for (int i = 0; i < question.getAnswersCount(); i++) {
            rb[i] = new RadioButton(getContext());
            rb[i].setText(answers.get(i).getText());
            rg.addView(rb[i]);
        }
        flAnswers.addView(rg);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                listener.setNextQuestion(question.getOrder() - 1);
            }
        });
    }

    public interface OnChooseAnswerListener {
        void setNextQuestion(int currentQuestionNr);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OnChooseAnswerListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnChooseAnswerListener");
        }
    }

}
