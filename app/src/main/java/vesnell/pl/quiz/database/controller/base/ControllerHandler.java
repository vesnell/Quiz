package vesnell.pl.quiz.database.controller.base;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * Created by alek6 on 20.04.2016.
 */
public class ControllerHandler {
    private static final String NAME = "ControllerThread";
    private static final ControllerHandler instance = new ControllerHandler();

    public static ControllerHandler getInstance() {
        return instance;
    }

    private final Handler handler;

    private ControllerHandler() {
        HandlerThread handlerThread = new HandlerThread(NAME);
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    public void execute(Runnable runnable) {
        handler.post(runnable);
    }
}
