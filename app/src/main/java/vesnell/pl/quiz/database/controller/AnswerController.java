package vesnell.pl.quiz.database.controller;

import android.content.Context;
import android.os.Handler;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.sql.SQLException;
import java.util.List;

import vesnell.pl.quiz.database.DBHelper;
import vesnell.pl.quiz.database.controller.base.BaseController;
import vesnell.pl.quiz.database.controller.base.CallbackRunnable;
import vesnell.pl.quiz.database.controller.base.ControllerHandler;
import vesnell.pl.quiz.database.controller.base.ControllerRunnable;
import vesnell.pl.quiz.database.model.Answer;
import vesnell.pl.quiz.database.model.Question;
import vesnell.pl.quiz.database.model.Quiz;

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

    public void requestList(final Question question) {
        ControllerHandler.getInstance().execute(new ControllerRunnable() {
            @Override
            protected void runController() {
                RuntimeExceptionDao<Question, Integer> questionDao
                        = OpenHelperManager.getHelper(context, DBHelper.class)
                        .getRuntimeExceptionDao(Question.class);
                Question refreshQuestion = questionDao.queryForId(question.getId());
                List<Answer> answers = refreshQuestion.getAnswers();
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
                try {
                    for (Answer answer : answers) {
                        Question question = answer.getQuestion();
                        if (getCount("question_id", question.getId()) < Answer.ANSWERS_IN_ONE_QUESTION_COUNT) {
                            create(answer);
                        }
                    }
                    handler.onAnswersListSaved(true);
                } catch(SQLException e) {
                    handler.onAnswersListSaved(false);
                }
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