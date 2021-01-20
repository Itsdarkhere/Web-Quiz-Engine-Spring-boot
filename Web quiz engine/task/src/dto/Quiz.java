package dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private long creatorId;
    private String title;
    private String text;
    private String[] options;

    @JsonIgnore
    private int[] answer;

    Quiz() {
    }

    Quiz(String title, String text, String[] options, int[] answer) {
        this.title = title;
        this.text = text;
        this.options = options;
        this.answer = answer;

    }

    @JsonIgnore
    public long getCreatorId() {
        return creatorId;
    }

    @JsonProperty
    public void setCreatorId(long creatorId) {
        this.creatorId = creatorId;
    }

    public int getId() {
        return id;
    }

    @JsonIgnore
    public int[] getAnswer() {
        return answer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;

    }

    public String[] getOptions() {
        return options;
    }

    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty
    public void setAnswer(int[] answer) {
        this.answer = answer;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

}