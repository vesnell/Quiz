package vesnell.pl.quiz.json;

/**
 * Created by alek6 on 19.04.2016.
 */
public interface JsonTags {
    String title = "title";
    String mainPhoto = "mainPhoto";
    String id = "id";
    String items = "items";
    String questions = "questions";
    String answer = "answer";
    String url = "url";
    String text = "text";
    String order = "order";
    String image = "image";
    String answers = "answers";
    String isCorrect = "isCorrect";

    //quiz type
    String type = "type";
    String KNOWLEDGE = "KNOWLEDGE";

    //answer types, key answer
    String ANSWER_TEXT_IMAGE = "ANSWER_TEXT_IMAGE";
    String ANSWER_TEXT = "ANSWER_TEXT";
    String ANSWER_IMAGE = "ANSWER_IMAGE";

    //question types, key type
    String QUESTION_TEXT_IMAGE = "QUESTION_TEXT_IMAGE";
    String QUESTION_TEXT = "QUESTION_TEXT";
    String QUESTION_IMAGE = "QUESTION_IMAGE";
}
