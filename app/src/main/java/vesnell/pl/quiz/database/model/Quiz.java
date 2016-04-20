package vesnell.pl.quiz.database.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONObject;

import java.io.Serializable;

import vesnell.pl.quiz.json.JsonTags;

/**
 * Created by alek6 on 19.04.2016.
 */
@DatabaseTable(tableName="Quiz")
public class Quiz implements Serializable {

    @DatabaseField(id = true)
    private String id;
    @DatabaseField
    private String title;
    @DatabaseField
    private String mainPhoto;
    @DatabaseField
    private Integer state;
    @DatabaseField
    private Integer questionsCount;
    @DatabaseField
    private Integer correctAnswers;

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
        return (correctAnswers * 100) / questionsCount;
    }

    public boolean hasScore() {
        return correctAnswers != null;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public boolean hasState() {
        return state != null;
    }

    public Integer getQuestionsCount() {
        return questionsCount;
    }

    public void setQuestionsCount(Integer questionsCount) {
        this.questionsCount = questionsCount;
    }

    public Integer getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(Integer correctAnswers) {
        this.correctAnswers = correctAnswers;
    }
}
