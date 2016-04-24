package vesnell.pl.quiz.android.questions;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import vesnell.pl.quiz.R;
import vesnell.pl.quiz.database.model.Answer;
import vesnell.pl.quiz.database.model.Question;
import vesnell.pl.quiz.database.model.Quiz;

/**
 * Created by ascen on 2016-04-22.
 */
public class QuestionFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {

    private static final String TAG = "QuestionFragment";

    private static final String POSITION = "position";
    private OnChooseAnswerListener listener;
    private TextView tvQuestionText;
    private ImageView ivQuestionImage;
    private boolean isQuestionAnswered = false;
    private List<Answer> answers;

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
        answers = question.getAnswers();
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

    private void createAnswers(FrameLayout flAnswers, Question question) {
        Answer.Type answerType = question.getAnswerType();
        switch (answerType) {
            case TEXT:
                setTextAnswers(flAnswers, question);
                break;
            case IMAGE:
                setImageAnswers(flAnswers, question);
                break;
            case TEXT_IMAGE:
                setTextImageAnswers(flAnswers, question);
                break;
        }

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        Answer answer = getAnswer(checkedId, answers);
        if (answer != null) {
            setNextQuestion(answer.isCorrect());
        } else {
            Log.e(TAG, "Unknown radio button ID");
        }
    }

    private void setTextImageAnswers(FrameLayout flAnswers, Question question) {
        final RadioButton[] radioButtons = new RadioButton[answers.size()];
        final RadioGroup rg = new RadioGroup(getContext());
        rg.setOrientation(RadioGroup.VERTICAL);
        rg.setOnCheckedChangeListener(this);
        for (int i = 0; i < question.getAnswersCount(); i++) {
            final int finalI = i;
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Drawable drawable = new BitmapDrawable(getContext().getResources(), bitmap);
                    radioButtons[finalI] = new RadioButton(getContext());
                    radioButtons[finalI].setId(answers.get(finalI).getId());
                    radioButtons[finalI].setText(answers.get(finalI).getText());
                    radioButtons[finalI].setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
                    rg.addView(radioButtons[finalI]);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            };
            Picasso.with(getContext()).load(answers.get(finalI).getImage()).into(target);
        }
        flAnswers.addView(rg);
    }

    private void setImageAnswers(FrameLayout flAnswers, Question question) {
        final RadioButton[] radioButtons = new RadioButton[answers.size()];
        final RadioGroup rg = new RadioGroup(getContext());
        rg.setOrientation(RadioGroup.VERTICAL);
        rg.setOnCheckedChangeListener(this);
        for (int i = 0; i < question.getAnswersCount(); i++) {
            final int finalI = i;
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Drawable drawable = new BitmapDrawable(getContext().getResources(), bitmap);
                    radioButtons[finalI] = new RadioButton(getContext());
                    radioButtons[finalI].setId(answers.get(finalI).getId());
                    radioButtons[finalI].setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
                    rg.addView(radioButtons[finalI]);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            };
            Picasso.with(getContext()).load(answers.get(finalI).getImage()).into(target);
        }
        flAnswers.addView(rg);
    }

    private void setTextAnswers(FrameLayout flAnswers, final Question question) {
        RadioGroup rg = new RadioGroup(getContext());
        final RadioButton[] rb = new RadioButton[question.getAnswersCount()];
        rg.setOrientation(RadioGroup.VERTICAL);
        rg.setOnCheckedChangeListener(this);
        for (int i = 0; i < question.getAnswersCount(); i++) {
            rb[i] = new RadioButton(getContext());
            rb[i].setText(answers.get(i).getText());
            rb[i].setId(answers.get(i).getId());
            rg.addView(rb[i]);
        }
        flAnswers.addView(rg);
    }

    private Answer getAnswer(int id, List<Answer> answers) {
        for (Answer answer : answers) {
            if (answer.getId() == id) {
                return answer;
            }
        }
        return null;
    }

    private void setNextQuestion(boolean isCorrectAnswer) {
        if (!isQuestionAnswered) {
            isQuestionAnswered = true;
            listener.setNextQuestion(isCorrectAnswer);
        }
    }

    public interface OnChooseAnswerListener {
        void setNextQuestion(boolean isCorrectAnswer);
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
