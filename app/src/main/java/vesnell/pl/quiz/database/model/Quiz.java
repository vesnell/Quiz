package vesnell.pl.quiz.database.model;

import android.util.Log;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONObject;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import vesnell.pl.quiz.json.JsonTags;

/**
 * Created by alek6 on 19.04.2016.
 */
@DatabaseTable(tableName="Quiz")
public class Quiz implements Serializable, Comparable<Quiz> {

    private static final String TAG = "Quiz";

    public static final String NAME = "quiz";

    @DatabaseField(id = true)
    private String id;
    @DatabaseField
    private String title;
    @DatabaseField
    private String mainPhoto;
    @DatabaseField(canBeNull = false, defaultValue = "0")
    private Integer state;
    @DatabaseField
    private int questionsCount;
    @DatabaseField(canBeNull = false, defaultValue = "0")
    private int correctAnswers;
    @DatabaseField(canBeNull = false, defaultValue = "0")
    private int lastScore;
    @ForeignCollectionField(eager = true)
    private ForeignCollection<Question> questions;

    //for OrmLite
    public Quiz() {
    }

    public Quiz(JSONObject item) {
        String title = item.optString(JsonTags.title);
        String id = item.optString(JsonTags.id);
        JSONObject mainPhotoJson = item.optJSONObject(JsonTags.mainPhoto);
        String mainPhoto = mainPhotoJson.optString(JsonTags.url);
        Integer questionsCount = item.optInt(JsonTags.questions);
        this.title = title;
        this.id = id;
        this.mainPhoto = mainPhoto;
        this.questionsCount = questionsCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMainPhoto() {
        return mainPhoto;
    }

    public void setMainPhoto(String mainPhoto) {
        this.mainPhoto = mainPhoto;
    }

    public Integer getScore() {
        return (lastScore * 100) / questionsCount;
    }

    public int getState() {
        return state;
    }

    public Integer getPercentState() {
        return (state * 100) / questionsCount;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean hasState() {
        return state != 0;
    }

    public Integer getQuestionsCount() {
        return questionsCount;
    }

    public void setQuestionsCount(Integer questionsCount) {
        this.questionsCount = questionsCount;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public int getLastScore() {
        return lastScore;
    }

    public void setLastScore(int lastScore) {
        this.lastScore = lastScore;
    }

    public boolean hasScore() {
        return lastScore > 0;
    }

    public List<Question> getQuestions() {
        try {
            if (questions != null) {
                questions.refreshCollection();
                return getSortedQuestionList();
            }
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        } catch (NullPointerException e) {
            return getSortedQuestionList();
        }
        return null;
    }

    private List<Question> getSortedQuestionList() {
        List<Question> list = new ArrayList<Question>(questions);
        Collections.sort(list);
        return list;
    }

    @Override
    public int compareTo(Quiz quiz) {
        return id.compareTo(quiz.id);
    }
}
