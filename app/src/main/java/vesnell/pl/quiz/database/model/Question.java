package vesnell.pl.quiz.database.model;

import android.util.Log;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import vesnell.pl.quiz.json.JsonTags;

/**
 * Created by ascen on 2016-04-21.
 */
@DatabaseTable(tableName="Question")
public class Question implements Serializable {

    private static final String TAG = "Question";

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private Quiz quiz;
    @DatabaseField
    private String text;
    @DatabaseField
    private int order;
    @DatabaseField
    private String image;
    @ForeignCollectionField(eager = true)
    private ForeignCollection<Answer> answers;

    private ArrayList<Answer> tempAnswers;

    public Question() {
    }

    public Question(JSONObject item, Quiz quiz) {
        String text = item.optString(JsonTags.text);
        int order = item.optInt(JsonTags.order);
        JSONObject jsonImage = item.optJSONObject(JsonTags.image);
        String image = jsonImage.optString(JsonTags.url);
        JSONArray jsonAnswers = item.optJSONArray(JsonTags.answers);
        ArrayList<Answer> answers = new ArrayList<Answer>();
        for (int i = 0; i < jsonAnswers.length(); i++) {
            JSONObject answerItem = jsonAnswers.optJSONObject(i);
            Answer answer = new Answer(answerItem);
            answers.add(answer);
        }
        this.quiz = quiz;
        this.text = text;
        this.order = order;
        this.image = image;
        this.tempAnswers = answers;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public ArrayList<Answer> getTempAnswers() {
        return tempAnswers;
    }
}
