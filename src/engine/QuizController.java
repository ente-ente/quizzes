package engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class QuizController {
    @Autowired
    QuizRepository quizRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;
    public QuizController() {

    }
    @PostMapping(path="/api/register", consumes = "application/json")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void register(@RequestBody @Valid User newUser) {
        User user = new User();
        user.setUsername(newUser.getUsername());
        user.setPassword(passwordEncoder.encode(newUser.getPassword()));
        try {
                userRepository.save(user);
        } catch(DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "username already taken"
            );
        }
    }

    @PostMapping(path="/api/quizzes", consumes = "application/json")
    public Quiz addNewQuiz(@Valid @RequestBody Quiz newQuiz) {
        quizRepository.save(newQuiz);
        return newQuiz;
    }

    @PostMapping(path = "/api/quizzes/{id}/solve", consumes = "application/json")
    public Assessment getAnswer(@PathVariable int id, @RequestBody Answer answer){
        var quiz = quizRepository.findById(id);
        if (quiz.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }
        Arrays.sort(answer.getAnswer());
        if (isCorrect(answer.getAnswer(), quiz.get())) {
            return new Assessment(true);
        } else {
            return new Assessment(false);
        }
    }

    private boolean isCorrect(int[] answer, Quiz quiz) {
        if (quiz.getAnswer() == null) {
            return answer.length == 0;
        }
        if (quiz.getAnswer().length != answer.length) {
            return false;
        }
        int[] correctAnswer = quiz.getAnswer();
        for (int i = 0; i < answer.length; i++) {
            if (correctAnswer[i] != answer[i]) {
                return false;
            }
        }
        return true;
    }

    @GetMapping(path = "/api/quizzes/{id}")
    public ResponseEntity<Quiz> getQuiz(@PathVariable int id){
        var quiz = quizRepository.findById(id);
        if (quiz.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }
        return ResponseEntity.ok(quiz.get());
    }

    @GetMapping(path = "/api/quizzes")
    public List<Quiz> getAllQuizzes() {
        return (List<Quiz>) quizRepository.findAll();
    }

/*    @PostMapping(path="/api/register", consumes = "application/json")
    public ResponseEntity register(@Valid @RequestBody User user) {
        if (checkCredentials(user)) {

        } else {
            return R
        }
    }*/
}
