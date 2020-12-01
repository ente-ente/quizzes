package engine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class QuizCompletion {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="QuizCompletionID")
    private int id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "UserID")
    private User user;


    @ManyToOne
    @JoinColumn(name = "QuizID")
    private Quiz quiz;

    LocalDateTime completedAt;

    public QuizCompletion() {}

    public QuizCompletion(User user, Quiz quiz, LocalDateTime completedAt) {
        this.user = user;
        this.quiz = quiz;
        this.completedAt = completedAt;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    @JsonProperty("id")
    public Integer getQuiz() {
        return quiz.getId();
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
