package vesnell.pl.quiz.model;

import org.json.JSONObject;

import java.io.Serializable;

import vesnell.pl.quiz.json.JsonTags;

/**
 * Created by alek6 on 19.04.2016.
 */
public class Quiz implements Serializable {

    private String id;
    private String title;
    private String mainPhoto;
    private Integer state;
    private Integer questionsCount;
    private Integer correctAnswers;

    public Quiz(JSONObject item) {
        String title = item.optString(JsonTags.title);
        String id = item.optString(JsonTags.id);
        String mainPhoto = item.optString(JsonTags.mainPhoto);
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
