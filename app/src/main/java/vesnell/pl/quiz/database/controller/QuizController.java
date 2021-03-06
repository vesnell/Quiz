package vesnell.pl.quiz.database.controller;

import android.content.Context;
import android.os.Handler;

import java.util.Collections;
import java.util.List;

import vesnell.pl.quiz.database.controller.base.BaseController;
import vesnell.pl.quiz.database.controller.base.CallbackRunnable;
import vesnell.pl.quiz.database.controller.base.ControllerHandler;
import vesnell.pl.quiz.database.controller.base.ControllerRunnable;
import vesnell.pl.quiz.database.model.Quiz;

/**
 * Created by alek6 on 20.04.2016.
 */
public class QuizController extends BaseController<Quiz> {

    private static final String TAG = "QuizController";

    public QuizController(Context context) {
        super(context, Quiz.class);
    }

    public interface QuizzesListLoadCallback {
        void onQuizzesListLoaded(List<Quiz> quizzes);
    }
    public interface QuizLoadCallback {
        void onQuizLoaded(Quiz quiz);
    }
    public interface QuizSaveCallback {
        void onQuizSaved(boolean result, Quiz quiz);
    }
    public interface QuizzesListSaveCallback {
        void onQuizzesListSaved(boolean result);
    }

    private QuizControllerHandler handler = new QuizControllerHandler();

    public void setQuizLoadCallback(QuizLoadCallback quizLoadCallback) {
        handler.quizLoadCallback = quizLoadCallback;
    }
    public void setQuizzesListLoadCallback(QuizzesListLoadCallback quizzesListLoadCallback) {
        handler.quizzesListLoadCallback = quizzesListLoadCallback;
    }
    public void setQuizSaveCallback(QuizSaveCallback quizSaveCallback) {
        handler.quizSaveCallback = quizSaveCallback;
    }
    public void setQuizzesListSaveCallback(QuizzesListSaveCallback quizzesListSaveCallback) {
        handler.quizzesListSaveCallback = quizzesListSaveCallback;
    }

    public void loadQuiz(final String quizId) {
        ControllerHandler.getInstance().execute(new ControllerRunnable() {
            @Override
            protected void runController() {
                Quiz quiz = get(quizId);
                handler.onQuizLoaded(quiz);
            }
        });
    }

    public void requestList() {
        ControllerHandler.getInstance().execute(new ControllerRunnable() {
            @Override
            protected void runController() {
                List<Quiz> quizzes = listAll();
                Collections.sort(quizzes, Collections.reverseOrder());
                handler.onQuizzesListLoaded(quizzes);
            }
        });
    }

    public void createQuiz(final Quiz quiz) {
        ControllerHandler.getInstance().execute(new ControllerRunnable() {
            @Override
            protected void runController() {
                handler.onQuizSaved(create(quiz), quiz);
            }
        });
    }

    public void saveQuizzesList(final List<Quiz> quizzes) {
        ControllerHandler.getInstance().execute(new ControllerRunnable() {
            @Override
            protected void runController() {
                for (Quiz quiz : quizzes) {
                    String quizId = quiz.getId();
                    if (get(quizId) == null) {
                        create(quiz);
                    }
                }
                handler.onQuizzesListSaved(true);
            }
        });
    }

    public void updateQuiz(final Quiz quiz) {
        ControllerHandler.getInstance().execute(new ControllerRunnable() {
            @Override
            protected void runController() {
                handler.onQuizSaved(update(quiz), quiz);
            }
        });
    }

    private static class QuizControllerHandler extends Handler implements QuizzesListLoadCallback,
            QuizzesListSaveCallback, QuizLoadCallback, QuizSaveCallback {

        private QuizzesListLoadCallback quizzesListLoadCallback;
        private QuizzesListSaveCallback quizzesListSaveCallback;
        private QuizSaveCallback quizSaveCallback;
        private QuizLoadCallback quizLoadCallback;

        @Override
        public void onQuizLoaded(final Quiz quiz) {
            this.post(new CallbackRunnable() {
                @Override
                protected void runCallback() {
                    quizLoadCallback.onQuizLoaded(quiz);
                }
            });
        }

        @Override
        public void onQuizSaved(final boolean result, final Quiz quiz) {
            this.post(new CallbackRunnable() {
                @Override
                protected void runCallback() {
                    quizSaveCallback.onQuizSaved(result, quiz);
                }
            });
        }

        @Override
        public void onQuizzesListLoaded(final List<Quiz> quizzes) {
            this.post(new CallbackRunnable() {
                @Override
                protected void runCallback() {
                    quizzesListLoadCallback.onQuizzesListLoaded(quizzes);
                }
            });
        }

        @Override
        public void onQuizzesListSaved(final boolean result) {
            this.post(new CallbackRunnable() {
                @Override
                protected void runCallback() {
                    quizzesListSaveCallback.onQuizzesListSaved(result);
                }
            });
        }
    }
}
