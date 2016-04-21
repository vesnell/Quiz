package vesnell.pl.quiz.database.controller;

import android.content.Context;
import android.os.Handler;

import java.util.List;

import vesnell.pl.quiz.database.controller.base.BaseController;
import vesnell.pl.quiz.database.controller.base.CallbackRunnable;
import vesnell.pl.quiz.database.controller.base.ControllerHandler;
import vesnell.pl.quiz.database.controller.base.ControllerRunnable;
import vesnell.pl.quiz.database.model.Answer;

/**
 * Created by ascen on 2016-04-21.
 */
public class AnswerController extends BaseController<Answer> {
    private static final String TAG = "AnswerController";

    public AnswerController(Context context) {
        super(context, Answer.class);
    }

    public interface AnswersListLoadCallback {
        void onAnswersListLoaded(List<Answer> answers);
    }
    public interface AnswerLoadCallback {
        void onAnswerLoaded(Answer answer);
    }
    public interface AnswerSaveCallback {
        void onAnswerSaved(boolean result, Answer answer);
    }
    public interface AnswersListSaveCallback {
        void onAnswersListSaved(boolean result);
    }

    private AnswerControllerHandler handler = new AnswerControllerHandler();

    public void setAnswerLoadCallback(AnswerLoadCallback answerLoadCallback) {
        handler.answerLoadCallback = answerLoadCallback;
    }
    public void setAnswersListLoadCallback(AnswersListLoadCallback answersListLoadCallback) {
        handler.answersListLoadCallback = answersListLoadCallback;
    }
    public void setAnswerSaveCallback(AnswerSaveCallback answerSaveCallback) {
        handler.answerSaveCallback = answerSaveCallback;
    }
    public void setAnswersListSaveCallback(AnswersListSaveCallback answersListSaveCallback) {
        handler.answersListSaveCallback = answersListSaveCallback;
    }

    public void requestList() {
        ControllerHandler.getInstance().execute(new ControllerRunnable() {
            @Override
            protected void runController() {
                List<Answer> answers = listAll();
                //Collections.sort(answers, new AnswerComparator());
                handler.onAnswersListLoaded(answers);
            }
        });
    }

    public void createAnswer(final Answer answer) {
        ControllerHandler.getInstance().execute(new ControllerRunnable() {
            @Override
            protected void runController() {
                handler.onAnswerSaved(create(answer), answer);
            }
        });
    }

    public void saveAnswersList(final List<Answer> answers) {
        ControllerHandler.getInstance().execute(new ControllerRunnable() {
            @Override
            protected void runController() {
                for (Answer answer : answers) {
                    create(answer);
                }
                handler.onAnswersListSaved(true);
            }
        });
    }

    private static class AnswerControllerHandler extends Handler implements AnswersListLoadCallback,
            AnswersListSaveCallback, AnswerLoadCallback, AnswerSaveCallback {

        private AnswersListLoadCallback answersListLoadCallback;
        private AnswersListSaveCallback answersListSaveCallback;
        private AnswerSaveCallback answerSaveCallback;
        private AnswerLoadCallback answerLoadCallback;

        @Override
        public void onAnswerLoaded(final Answer answer) {
            this.post(new CallbackRunnable() {
                @Override
                protected void runCallback() {
                    answerLoadCallback.onAnswerLoaded(answer);
                }
            });
        }

        @Override
        public void onAnswerSaved(final boolean result, final Answer answer) {
            this.post(new CallbackRunnable() {
                @Override
                protected void runCallback() {
                    answerSaveCallback.onAnswerSaved(result, answer);
                }
            });
        }

        @Override
        public void onAnswersListLoaded(final List<Answer> answers) {
            this.post(new CallbackRunnable() {
                @Override
                protected void runCallback() {
                    answersListLoadCallback.onAnswersListLoaded(answers);
                }
            });
        }

        @Override
        public void onAnswersListSaved(final boolean result) {
            this.post(new CallbackRunnable() {
                @Override
                protected void runCallback() {
                    answersListSaveCallback.onAnswersListSaved(result);
                }
            });
        }
    }
}