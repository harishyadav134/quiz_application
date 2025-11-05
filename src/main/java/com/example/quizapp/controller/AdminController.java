package com.example.quizapp.controller;

import com.example.quizapp.model.Level;
import com.example.quizapp.model.Question;
import com.example.quizapp.service.QuestionService;
import com.example.quizapp.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuizService quizService;

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/questions")
    public String listQuestions(Model model) {
        model.addAttribute("questions", questionService.getAllQuestions());
        // Success/error messages passed via RedirectAttributes are automatically added here.
        return "admin/question-list";
    }

    @GetMapping("/questions/add")
    public String showAddQuestionForm(Model model) {
        model.addAttribute("question", new Question());
        model.addAttribute("levels", Level.values());
        return "admin/question-form";
    }

    @PostMapping("/questions/add")
    public String addQuestion(@ModelAttribute("question") Question question, RedirectAttributes redirectAttributes) {
        questionService.saveOrUpdateQuestion(question);
        redirectAttributes.addFlashAttribute("successMessage", "Question added successfully!");
        return "redirect:/admin/questions";
    }

    @GetMapping("/questions/edit/{id}")
    public String showEditQuestionForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Question question = questionService.getQuestionById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid question Id: " + id));
            model.addAttribute("question", question);
            model.addAttribute("levels", Level.values());
            return "admin/question-form";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: Question not found.");
            return "redirect:/admin/questions";
        }
    }

    @PostMapping("/questions/edit/{id}")
    public String updateQuestion(@PathVariable Long id, @ModelAttribute("question") Question question, RedirectAttributes redirectAttributes) {
        question.setId(id);
        questionService.saveOrUpdateQuestion(question);
        redirectAttributes.addFlashAttribute("successMessage", "Question updated successfully!");
        return "redirect:/admin/questions";
    }

    @GetMapping("/questions/delete/{id}")
    public String deleteQuestion(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            questionService.deleteQuestion(id);
            redirectAttributes.addFlashAttribute("successMessage", "Question deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting question.");
        }
        return "redirect:/admin/questions";
    }

    @GetMapping("/scores")
    public String viewScores(Model model) {
        model.addAttribute("results", quizService.getAllResults());
        return "admin/user-scores";
    }
}