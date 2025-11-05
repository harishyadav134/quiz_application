package com.example.quizapp.controller;

import com.example.quizapp.model.Level;
import com.example.quizapp.model.Question;
import com.example.quizapp.model.LeaderboardDTO; 
import com.example.quizapp.service.QuizService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal; 
import java.util.List;
import java.util.Map;

@Controller
public class QuizController {

    @Autowired
    private QuizService quizService;

    @GetMapping("/user/dashboard")
    public String userDashboard() {
        return "user/dashboard";
    }

    
    @GetMapping("/user/leaderboard")
    public String showLeaderboard(Model model, Principal principal) {
        
        List<LeaderboardDTO> leaderboard = quizService.getLeaderboard();
        
        
        model.addAttribute("leaderboard", leaderboard);
        
        
        model.addAttribute("currentUser", principal.getName()); 
        
        return "user/leaderboard";
    }


    @GetMapping("/quiz/start")
    public String startQuiz(@RequestParam("level") Level level, HttpSession session, Model model) {
        List<Question> questions = quizService.getQuestionsForLevel(level, 15); 
        session.setAttribute("questions", questions);
        session.setAttribute("level", level);
        model.addAttribute("questions", questions);
        model.addAttribute("level", level);
        return "user/quiz";
    }

    @PostMapping("/quiz/submit")
    public String submitQuiz(@RequestParam Map<String, String> answers, HttpSession session, Authentication authentication, RedirectAttributes redirectAttributes) {
        List<Question> questions = (List<Question>) session.getAttribute("questions");
        Level level = (Level) session.getAttribute("level");

        
        java.util.Map<Long, String> userAnswers = new java.util.HashMap<>();
        for (Map.Entry<String, String> entry : answers.entrySet()) {
            if (entry.getKey().startsWith("q_")) {
                userAnswers.put(Long.parseLong(entry.getKey().substring(2)), entry.getValue());
            }
        }
        
        int score = quizService.calculateScore(questions, userAnswers);
        String username = authentication.getName();
        quizService.saveResult(username, level, score, questions.size());

        redirectAttributes.addFlashAttribute("score", score);
        redirectAttributes.addFlashAttribute("total", questions.size());
        redirectAttributes.addFlashAttribute("questions", questions);
        redirectAttributes.addFlashAttribute("userAnswers", userAnswers);

        session.removeAttribute("questions");
        session.removeAttribute("level");

        return "redirect:/quiz/result";
    }

    @GetMapping("/quiz/result")
    public String showResult(Model model) {
        if (!model.containsAttribute("score")) {
            return "redirect:/user/dashboard";
        }
        return "user/result";
    }
}