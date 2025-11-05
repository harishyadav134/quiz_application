package com.example.quizapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
@AllArgsConstructor
public class LeaderboardDTO {
    private String username;
    private Long totalWeightedScore;
}