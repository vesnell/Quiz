package vesnell.pl.quiz.database.controller;

import android.content.Context;
import android.os.Handler;

import java.util.List;

import vesnell.pl.quiz.database.controller.base.BaseController;
import vesnell.pl.quiz.database.controller.base.CallbackRunnable;
import vesnell.pl.quiz.database.controller.base.ControllerHandler;
import vesnell.pl.quiz.database.controller.base.ControllerRunnable;
import vesnell.pl.quiz.database.model.Question;

/**
 * Created by ascen on 2016-04-21.
 */
public class QuestionController extends BaseController<Question> {
    private static final String TAG = "QuestionController";

    public QuestionController(Context context) {
        super(context, Question.class);
    }

    public interface QuestionsListLoadCallback {
        void onQuestionsListLoaded(List<Question> questions);
    }
    public interface QuestionLoadCallback {
        void onQuestionLoaded(Question question);
    }
    public interface QuestionSaveCallback {
        void onQuestionSaved(boolean result, Question question);
    }
    public interface QuestionsListSaveCallback {
        void onQuestionsListSaved(boolean result);
    }

    private QuestionControllerHandler handler = new QuestionControllerHandler();

    public void setQuestionLoadCallback(QuestionLoadCallback questionLoadCallback) {
        handler.questionLoadCallback = questionLoadCallback;
    }
    public void setQuestionsListLoadCallback(QuestionsListLoadCallback questionsListLoadCallback) {
        handler.questionsListLoadCallback = questionsListLoadCallback;
    }
    public void setQuestionSaveCallback(QuestionSaveCallback questionSaveCallback) {
        handler.questionSaveCallback = questionSaveCallback;
    }
    public void setQuestionsListSaveCallback(QuestionsListSaveCallback questionsListSaveCallback) {
        handler.questionsListSaveCallback = questionsListSaveCallback;
    }

    public void requestList() {
        ControllerHandler.getInstance().execute(new ControllerRunnable() {
            @Override
            protected void runController() {
                List<Question> questions = listAll();
                //Collections.sort(questions, new QuestionComparator());
                handler.onQuestionsListLoaded(questions);
            }
        });
    }

    public void createQuestion(final Question question) {
        ControllerHandler.getInstance().execute(new ControllerRunnable() {
            @Override
            protected void runController() {
                handler.onQuestionSaved(create(question), question);
            }
        });
    }

    public void saveQuestionsList(final List<Question> questions) {
        ControllerHandler.getInstance().execute(new ControllerRunnable() {
            @Override
            protected void runController() {
                for (Question question : questions) {
                    create(question);
                }
                handler.onQuestionsListSaved(true);
            }
        });
    }

    private static class QuestionControllerHandler extends Handler implements QuestionsListLoadCallback,
            QuestionsListSaveCallback, QuestionLoadCallback, QuestionSaveCallback {

        private QuestionsListLoadCallback questionsListLoadCallback;
        private QuestionsListSaveCallback questionsListSaveCallback;
        private QuestionSaveCallback questionSaveCallback;
        private QuestionLoadCallback questionLoadCallback;

        @Override
        public void onQuestionLoaded(final Question question) {
            this.post(new CallbackRunnable() {
                @Override
                protected void runCallback() {
                    questionLoadCallback.onQuestionLoaded(question);
                }
            });
        }

        @Override
        public void onQuestionSaved(final boolean result, final Question question) {
            this.post(new CallbackRunnable() {
                @Override
                protected void runCallback() {
                    questionSaveCallback.onQuestionSaved(result, question);
                }
            });
        }

        @Override
        public void onQuestionsListLoaded(final List<Question> questions) {
            this.post(new CallbackRunnable() {
                @Override
                protected void runCallback() {
                    questionsListLoadCallback.onQuestionsListLoaded(questions);
                }
            });
        }

        @Override
        public void onQuestionsListSaved(final boolean result) {
            this.post(new CallbackRunnable() {
                @Override
                protected void runCallback() {
                    questionsListSaveCallback.onQuestionsListSaved(result);
                }
            });
        }
    }
}
