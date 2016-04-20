package vesnell.pl.quiz.database.controller.base;

import android.util.Log;

/**
 * Created by alek6 on 20.04.2016.
 */
public abstract class ControllerRunnable implements Runnable {
    @Override
    public final void run() {
        try {
            runController();
        } catch (Exception e) {
            Log.w(getClass().getName(), "ControllerRunnable exception", e);
        }
    }

    protected abstract void runController();
}
