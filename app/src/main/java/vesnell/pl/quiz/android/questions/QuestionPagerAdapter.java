package vesnell.pl.quiz.android.questions;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import vesnell.pl.quiz.database.model.Quiz;

/**
 * Created by ascen on 2016-04-22.
 */
public class QuestionPagerAdapter  extends FragmentPagerAdapter {

    private Quiz quiz;

    public QuestionPagerAdapter(FragmentManager fm, Quiz quiz) {
        super(fm);
        this.quiz = quiz;
    }

    @Override
    public Fragment getItem(int position) {
        return QuestionFragment.newInstance(position, quiz);
    }

    @Override
    public int getCount() {
        return quiz.getQuestionsCount();
    }
}
