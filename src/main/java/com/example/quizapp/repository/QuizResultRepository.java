package com.example.quizapp.repository;

import com.example.quizapp.model.LeaderboardDTO;
import com.example.quizapp.model.QuizResult;
import com.example.quizapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {

    List<QuizResult> findByUserOrderByTimestampDesc(User user);

   
    
    @Query("SELECT new com.example.quizapp.model.LeaderboardDTO(r.user.username, " +
           "SUM(CASE r.level " +
           "    WHEN com.example.quizapp.model.Level.EASY THEN r.score * 1 " +
           "    WHEN com.example.quizapp.model.Level.MEDIUM THEN r.score * 2 " +
           "    WHEN com.example.quizapp.model.Level.HARD THEN r.score * 3 " +
           "    ELSE 0 END)) " +
           "FROM QuizResult r " +
           "GROUP BY r.user.username " +
           "ORDER BY SUM(CASE r.level " +
           "    WHEN com.example.quizapp.model.Level.EASY THEN r.score * 1 " +
           "    WHEN com.example.quizapp.model.Level.MEDIUM THEN r.score * 2 " +
           "    WHEN com.example.quizapp.model.Level.HARD THEN r.score * 3 " +
           "    ELSE 0 END) DESC")
    List<LeaderboardDTO> findLeaderboard();
}