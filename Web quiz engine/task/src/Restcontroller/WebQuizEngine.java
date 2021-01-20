package Restcontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.Quiz;
import dto.SolverInfo;
import dto.User;
import dto.logPass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import repositories.QuizRepository;
import repositories.SolverInfoRepository;
import repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.*;



@SpringBootApplication
public class WebQuizEngine {

    public static void main(String[] args) {

        SpringApplication.run(WebQuizEngine.class, args);

    }

}

@EnableWebSecurity
@RestController
class QuizControl {

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private QuizRepository repository;

    @Autowired
    private SolverInfoRepository solvedRepository;


    QuizControl() {}

    

        @PostMapping(path = "/actuator/shutdown")
        void killApp() {
            //kills app
        }


        @DeleteMapping(path = "/api/quizzes/{id}")
        String deleteRequest(@PathVariable long id) {
            long creatorId = -1;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            for (User user: userRepository.findAll()) {
                if (user.getUsername().equals(authentication.getName())) {
                    creatorId = user.getId();
                }
            }

            for (Quiz quiz: repository.findAll()) {
                if (quiz.getId() == id) {
                    if (quiz.getCreatorId() == creatorId) {
                        repository.delete(quiz);
                        throw new ResponseStatusException(HttpStatus.NO_CONTENT);
                    } else {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
                    }
                }
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        }


        @PostMapping(path = "/api/register")
        void postRegistration(@RequestBody logPass logPass) {
            if (userRepository.findByUsername(logPass.getEmail()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

            } else if (logPass.getPassword().length() < 5) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

            } else if (!(logPass.getEmail().contains("@") && logPass.getEmail().contains("."))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }

            User newUser = new User();
            newUser.setUsername(logPass.getEmail());
            newUser.setPassword(passwordEncoder.encode(logPass.getPassword()));
            userRepository.save(newUser);


        }

        @GetMapping(path = "/api/quizzes/{id}")
        String getQuiz(@PathVariable int id) throws JsonProcessingException {
            for (Quiz q: repository.findAll()) {
                if (q.getId() == id) {
                    return mapper.writeValueAsString(q);
                }
            }
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );

        }

        @GetMapping(path = "/api/quizzes/completed")
        Slice<SolverInfo> getSolved(@RequestParam(defaultValue = "0") int page) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Pageable paging = PageRequest.of(page, 10, Sort.by("completedAt").descending());

            return solvedRepository.findAllByEmail(auth.getName(), paging);

        }


        @GetMapping(path="/api/quizzes")
        Page<Quiz> getQuizzes(@RequestParam(defaultValue = "0") int page) throws JsonProcessingException {

            Pageable paging = PageRequest.of(page, 10);
            Page<Quiz> quizPage = repository.findAll(paging);

            if (quizPage.hasContent()) {

                return quizPage;

            }
            return quizPage;
        }

        @PostMapping(path="/api/quizzes")
        String postQuiz(@RequestBody Quiz quiz) {
            long creatorId = -1;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            for (User r: userRepository.findAll()) {
                if (r.getUsername().equals(authentication.getName())) {
                    creatorId = r.getId();
                }

            }

            //Making sure the quiz is a proper quiz
            if (quiz.getTitle() == null || quiz.getTitle().equals("")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            } else if (quiz.getText() == null || quiz.getText().equals("")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            } else if (quiz.getOptions() == null || quiz.getOptions().length < 2) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }

            quiz.setCreatorId(creatorId);
            repository.save(quiz);
            String response = null;

            try {
                response =  mapper.writeValueAsString(quiz);
            } catch (JsonProcessingException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }

            return response;

        }

        @PostMapping(path="/api/quizzes/{id}/solve")
        String solveQuiz(@RequestBody Quiz quiz, @PathVariable int id) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            SolverInfo solver = new SolverInfo(auth.getName(),id, LocalDateTime.now());

            int[] answer = quiz.getAnswer();
            Arrays.sort(answer);
            Boolean isCorrect = true;

            for (Quiz q: repository.findAll()) {
                if (q.getId() == id) {

                    if (q.getAnswer() == null) {
                        if (answer.length == 0) {
                            solvedRepository.save(solver);
                            return "{\"success\":true,\"feedback\":\"" +
                                    "Congratulations, you're right!\"}";
                        } else {

                            return "{\"success\":false,\"feedback\":\"" +
                                    "Wrong answer! Please, try again.\"}";
                        }
                    }

                    if (answer.length != q.getAnswer().length){
                        isCorrect = false;


                    } else {
                        Arrays.sort(q.getAnswer());
                        for (int i = 0; i < answer.length; i++) {
                            if (q.getAnswer()[i] != answer[i]) {
                                isCorrect = false;
                                break;
                            }
                        }
                    }

                    if (isCorrect) {
                        solvedRepository.save(solver);
                        return "{\"success\":true,\"feedback\":\"" +
                                "Congratulations, you're right!\"}";
                    } else {
                        return "{\"success\":false,\"feedback\":\"" +
                                "Wrong answer! Please, try again.\"}";
                    }
                }
            }
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );

        }
}


