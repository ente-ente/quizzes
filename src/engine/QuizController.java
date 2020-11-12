package engine;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;

@RestController
public class QuizController {

    ArrayList<Quiz> quizzes = new ArrayList<>();

    public QuizController() {

    }

    @PostMapping(path="/api/quizzes", consumes = "application/json")
    public Quiz addNewQuiz(@RequestBody Quiz newQuiz) {
        // id must be unique, even if quizzes are removed
        if (quizzes.size() > 0) {
            newQuiz.setId(quizzes.get(quizzes.size() - 1).getId() + 1);
        } else {
            newQuiz.setId(1);
        }
        quizzes.add(newQuiz);
        return newQuiz;
    }

    @PostMapping(path = "/api/quizzes/{id}/solve", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Answer getAnswer(@PathVariable int id, int answer){
        var quiz = quizzes.stream().filter(q -> q.getId() == id).findAny().orElse(null);
        if (quiz == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }
        if (answer == quiz.getAnswer()) {
            return new Answer(true);
        } else {
            return new Answer(false);
        }
    }

    @GetMapping(path = "/api/quizzes/{id}")
    public ResponseEntity<Quiz> getQuiz(@PathVariable int id){
        var quiz = quizzes.stream().filter(q -> q.getId() == id).findAny().orElse(null);
        if (quiz == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }
        return ResponseEntity.ok(quiz);
    }

    @GetMapping(path = "/api/quizzes")
    public ArrayList<Quiz> getAllQuizzes() {
        return quizzes;
    }
}
