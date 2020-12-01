package engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@RestController
public class QuizController {
    @Autowired
    QuizRepository quizRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;
    @Autowired
    QuizCompletionRepository quizCompletionRepository;
    public QuizController() {}
    @PostMapping(path="/api/register", consumes = "application/json")
    @ResponseStatus(code = HttpStatus.OK)
    public void register(@RequestBody @Valid User newUser) {
        User user = new User();

        user.setUsername(newUser.getUsername());

        user.setPassword(passwordEncoder.encode(newUser.getPassword()));
        try {
                userRepository.save(user);
        } catch(DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage()
            );
        }
    }

    @PostMapping(path="/api/quizzes", consumes = "application/json")
    public Quiz addNewQuiz(@Valid @RequestBody Quiz newQuiz, Principal principal) {
        newQuiz.setUser(userRepository.findByUsername(principal.getName()));
        quizRepository.save(newQuiz);
        return newQuiz;
    }

    @PostMapping(path = "/api/quizzes/{id}/solve", consumes = "application/json")
    public Assessment getAnswer(@PathVariable int id, @RequestBody Answer answer, Principal principal){
        var quiz = quizRepository.findById(id);
        if (quiz.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }
        Arrays.sort(answer.getAnswer());
        if (isCorrect(answer.getAnswer(), quiz.get())) {
            quizCompletionRepository.save(
                    new QuizCompletion(
                            userRepository.findByUsername(principal.getName()), quiz.get(), LocalDateTime.now()));
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

    @GetMapping(path = "/api/quizzes/me")
    public ResponseEntity<Page<Quiz>> getAllQuizzesForUser(@RequestParam(defaultValue = "0") Integer page,
                                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                                    @RequestParam(defaultValue = "id") String sortBy,
                                                    Principal principal) {
        return ResponseEntity.ok(quizRepository.findQuizzesForUser(userRepository.findByUsername(principal.getName()), PageRequest.of(page, pageSize, Sort.by(sortBy))));
    }

    @GetMapping(path = "/api/quizzes")
    public ResponseEntity<Page<Quiz>> getAllQuizzes(@RequestParam(defaultValue = "0") Integer page,
                                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                                    @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(quizRepository.findAll(PageRequest.of(page, pageSize, Sort.by(sortBy))));
    }


    @GetMapping(path = "/api/quizzes/completed")
    public ResponseEntity<Page<QuizCompletion>> getAllQuizCompletions(@RequestParam(defaultValue = "0") Integer page,
                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                    @RequestParam(defaultValue = "completedAt") String sortBy, Principal principal) {

        return ResponseEntity.ok(quizCompletionRepository.findQuizCompletionsForUser(userRepository.findByUsername(principal.getName()), PageRequest.of(page, pageSize, Sort.by(sortBy).descending())));
    }

    @DeleteMapping(value = "/api/quizzes/{id}")
    public ResponseEntity<Integer> deletePost(@PathVariable int id, Principal principal) {
        Optional<Quiz> toDelete = quizRepository.findById(id);
        if (toDelete.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (toDelete.get().getUser().getUsername() != principal.getName()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        quizRepository.deleteById(id);
        return new ResponseEntity<>(id, HttpStatus.NO_CONTENT);
    }
}
