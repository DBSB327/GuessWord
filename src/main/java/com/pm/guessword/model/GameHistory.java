package com.pm.guessword.model;

import com.pm.guessword.enums.GameStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "game_history")
public class GameHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(nullable = false)
    private String guessedWord;

    @Column(nullable = false)
    private boolean correct;

    private LocalDateTime date = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private GameStatus status = GameStatus.IN_PROGRESS;

    private int attempts;

    @Column(nullable = false)
    private int maxAttempts;

    private int earnedScore;

    private int remainingWordGuesses;
}
