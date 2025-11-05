package com.example.quizapp.service;

import com.example.quizapp.model.*;
import com.example.quizapp.repository.QuestionRepository;
import com.example.quizapp.repository.QuizResultRepository;
import com.example.quizapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class QuizService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizResultRepository quizResultRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Question> getQuestionsForLevel(Level level, int numQuestions) {
        List<Question> questions = questionRepository.findByLevel(level);
        Collections.shuffle(questions);
        return questions.subList(0, Math.min(numQuestions, questions.size()));
    }

    public int calculateScore(List<Question> questions, Map<Long, String> userAnswers) {
        int score = 0;
        for (Question question : questions) {
            String correctAnswer = question.getCorrectAnswer();
            String userAnswer = userAnswers.get(question.getId());
            if (correctAnswer.equals(userAnswer)) {
                score++;
            }
        }
        return score;
    }

    public void saveResult(String username, Level level, int score, int totalQuestions) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        QuizResult result = new QuizResult();
        result.setUser(user);
        result.setLevel(level);
        result.setScore(score);
        result.setTotalQuestions(totalQuestions);
        quizResultRepository.save(result);
    }
    
    public List<QuizResult> getAllResults() {
        return quizResultRepository.findAll();
    }

    // --- THIS IS THE NEW METHOD ---
    public List<LeaderboardDTO> getLeaderboard() {
        return quizResultRepository.findLeaderboard();
    }
}