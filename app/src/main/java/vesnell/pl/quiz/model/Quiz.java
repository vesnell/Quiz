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
    private Integer score;
    private Integer state;

    public Quiz(JSONObject item) {
        String title = item.optString(JsonTags.title);
        String id = item.optString(JsonTags.id);
        String mainPhoto = item.optString(JsonTags.mainPhoto);
        this.title = title;
        this.id = id;
        this.mainPhoto = mainPhoto;
    }

    public Quiz(String id, String title, String mainPhoto, Integer score, Integer state) {
        this.id = id;
        this.title = title;
        this.mainPhoto = mainPhoto;
        this.score = score;
        this.state = state;
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
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
