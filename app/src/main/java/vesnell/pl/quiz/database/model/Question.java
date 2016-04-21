package vesnell.pl.quiz.database.model;

import android.util.Log;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ascen on 2016-04-21.
 */
@DatabaseTable(tableName="Question")
public class Question {

    private static final String TAG = "Question";

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private Quiz quiz;
    @DatabaseField
    private String text;
    @DatabaseField
    private int order;
    @ForeignCollectionField(eager = true)
    private ForeignCollection<Answer> answers;

    public Question() {
    }

    public int getId() {
        return id;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public List<Answer> getAnswers() {
        try {
            if (answers != null) {
                answers.refreshCollection();
                return new ArrayList<Answer>(answers);
            }
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        } catch (NullPointerException e) {
            return new ArrayList<Answer>(answers);
        }
        return null;
    }
}
