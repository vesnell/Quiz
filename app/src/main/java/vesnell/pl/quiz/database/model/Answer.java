package vesnell.pl.quiz.database.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONObject;

import java.util.AbstractMap;

import vesnell.pl.quiz.json.JsonTags;

/**
 * Created by ascen on 2016-04-21.
 */
@DatabaseTable(tableName="Answer")
public class Answer {

    @DatabaseField(generatedId = true)
    private String id;
    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private Question question;
    @DatabaseField
    private String text;
    @DatabaseField
    private int order;

    public Answer() {
    }

    public Answer(JSONObject item) {
        String text = item.optString(JsonTags.text);
        int order = item.optInt(JsonTags.order);
        this.text = text;
        this.order = order;
    }

    public Answer(Question question, Answer answer) {
        this.question = question;
        this.text = answer.getText();
        this.order = answer.getOrder();
    }

    public String getId() {
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
}
