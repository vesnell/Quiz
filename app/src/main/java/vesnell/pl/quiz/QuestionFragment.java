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

    private static final String POSITION = "position";
    private OnChooseAnswerListener listener;
    private TextView tvQuestionText;
    private ImageView ivQuestionImage;

    public static QuestionFragment newInstance(int position, Quiz quiz) {
        QuestionFragment questionFragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        args.putSerializable(Quiz.NAME, quiz);
        questionFragment.setArguments(args);
        return questionFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_question, container, false);
        Quiz quiz = (Quiz) getArguments().getSerializable(Quiz.NAME);
        int position = getArguments().getInt(POSITION);

        tvQuestionText = (TextView) v.findViewById(R.id.questionText);
        ivQuestionImage = (ImageView) v.findViewById(R.id.questionImage);
        FrameLayout flAnswers = (FrameLayout) v.findViewById(R.id.answers_view);

        List<Question> questions = quiz.getQuestions();
        Question question = questions.get(position);
        createQuestion(question);
        createAnswers(flAnswers, question);

        return v;
    }

    private void createQuestion(Question question) {
        Question.Type questionType = question.getType();
        String questionText = question.getText();
        String imageUrl = question.getImage();

        switch (questionType) {
            case TEXT:
                tvQuestionText.setText(questionText);
                ivQuestionImage.setVisibility(View.GONE);
                break;
            case IMAGE:
                tvQuestionText.setVisibility(View.GONE);
                Picasso.with(getContext()).load(imageUrl)
                        .resizeDimen(R.dimen.question_image_width, R.dimen.question_image_height).centerCrop().into(ivQuestionImage);
                break;
            case TEXT_IMAGE:
                tvQuestionText.setText(questionText);
                Picasso.with(getContext()).load(imageUrl)
                        .resizeDimen(R.dimen.question_image_width, R.dimen.question_image_height).centerCrop().into(ivQuestionImage);
                break;
        }
    }

    private void createAnswers(FrameLayout flAnswers, final Question question) {
        final RadioButton[] rb = new RadioButton[question.getAnswersCount()];
        RadioGroup rg = new RadioGroup(getContext());
        rg.setOrientation(RadioGroup.VERTICAL);
        List<Answer> answers = question.getAnswers();
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
