package vesnell.pl.quiz.database.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONObject;

import java.io.Serializable;

import vesnell.pl.quiz.json.JsonTags;

/**
 * Created by ascen on 2016-04-21.
 */
@DatabaseTable(tableName="Answer")
public class Answer implements Serializable, Comparable<Answer> {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private Question question;
    @DatabaseField
    private String text;
    @DatabaseField
    private int order;
    @DatabaseField
    private boolean isCorrect;
    @DatabaseField
    private String image;

    public enum Type {
        TEXT(JsonTags.ANSWER_TEXT),
        IMAGE(JsonTags.ANSWER_IMAGE),
        TEXT_IMAGE(JsonTags.ANSWER_TEXT_IMAGE);

        private String type;

        Type(String type) {
            this.type = type;
        }

        private String getType() {
            return type;
        }

        public static Type getType(String t) {
            for (Type type : Type.values()) {
                if (type.getType().equals(t)) {
                    return type;
                }
            }
            return null;
        }
    }

    public Answer() {
    }

    public Answer(JSONObject item) {
        String text = item.optString(JsonTags.text);
        int order = item.optInt(JsonTags.order);
        int isCorrect = item.optInt(JsonTags.isCorrect);
        JSONObject jsonImage = item.optJSONObject(JsonTags.image);
        String image = jsonImage.optString(JsonTags.url);
        this.text = text;
        this.order = order;
        this.isCorrect = isCorrect == 1;
        this.image = image;
    }

    public Answer(Question question, Answer answer) {
        this.question = question;
        this.text = answer.getText();
        this.order = answer.getOrder();
        this.isCorrect = answer.isCorrect();
        this.image = answer.getImage();
    }

    public int getId() {
        return id;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
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

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int compareTo(Answer answer) {
        return order < answer.order ? -1 : 1;
    }
}
