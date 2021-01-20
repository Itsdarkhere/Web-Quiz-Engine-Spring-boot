package dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class SolverInfo {

    @Id
    //@JsonIgnore
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    @JsonIgnore
    private String email;
    private int quizId;
    private LocalDateTime completedAt;

    SolverInfo() {}


    public SolverInfo (String email, int quizId, LocalDateTime completedAt) {
        this.email = email;
        this.quizId = quizId;
        this.completedAt = completedAt;

    }
    @JsonIgnore
    public String getEmail() {
        return email;
    }


    public int getId() {
        return quizId;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

}