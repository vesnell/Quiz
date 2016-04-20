package vesnell.pl.quiz.database.controller.base;

import android.util.Log;

/**
 * Created by alek6 on 20.04.2016.
 */
public abstract class CallbackRunnable implements Runnable {
    @Override
    public final void run() {
        try {
            runCallback();
        } catch (Exception e) {
            Log.w(getClass().getName(), "CallbackRunnable exception", e);
        }
    }

    protected abstract void runCallback();
}
