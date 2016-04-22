package vesnell.pl.quiz.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by ascen on 2016-04-22.
 */
public class NonTouchableViewPager extends ViewPager {

    private boolean isTouchable = false;

    public NonTouchableViewPager(Context context) {
        super(context);
    }

    public NonTouchableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean isTouchable() {
        return isTouchable;
    }

    public void setTouchable(boolean touchable) {
        isTouchable = touchable;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return isTouchable && super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return isTouchable && super.onTouchEvent(event);
    }
}
