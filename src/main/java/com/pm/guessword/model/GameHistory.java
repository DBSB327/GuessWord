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

    private boolean correct;

    private LocalDateTime date = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    private int attempts;
}
