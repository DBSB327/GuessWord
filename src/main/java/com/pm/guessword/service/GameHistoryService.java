package com.pm.guessword.service;

import com.pm.guessword.dto.GameHistoryResponse;
import com.pm.guessword.enums.GameStatus;
import com.pm.guessword.mapper.GameHistoryMapper;
import com.pm.guessword.model.GameHistory;
import com.pm.guessword.model.Question;
import com.pm.guessword.model.User;
import com.pm.guessword.repository.GameHistoryRepository;
import com.pm.guessword.repository.QuestionRepository;
import com.pm.guessword.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameHistoryService {

    private final GameHistoryRepository gameHistoryRepository;
    private final GameHistoryMapper gameHistoryMapper;
    private final QuestionRepository questionRepository;
    private final UserService userService;

    @Transactional
    public GameHistoryResponse startGame() {
        User user = userService.getCurrentUser();
        Question question = questionRepository.findRandomQuestion();

        if (question == null) {
            throw new EntityNotFoundException("Not available question");
        }

        GameHistory game = new GameHistory();
        game.setUser(user);
        game.setQuestion(question);
        game.setGuessedWord("_".repeat(question.getAnswer().length()));
        game.setCorrect(false);
        game.setAttempts(0);
        game.setStatus(GameStatus.IN_PROGRESS);

        var saved = gameHistoryRepository.save(game);
        return gameHistoryMapper.toResponse(saved);
    }

    @Transactional
    public GameHistoryResponse guessLetter(Long gameId, char letter) {
        GameHistory game = gameHistoryRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found"));

        if (game.getStatus().equals(GameStatus.FINISHED)) {
            throw new IllegalStateException("Game is already finished");
        }

        String answer = game.getQuestion().getAnswer().toLowerCase();
        String current = game.getGuessedWord();
        StringBuilder revealed = new StringBuilder(current);

        for (int i = 0; i < answer.length(); i++) {
            if (answer.charAt(i) == Character.toLowerCase(letter)) {
                revealed.setCharAt(i, answer.charAt(i));

            }
        }
        game.setGuessedWord(revealed.toString());
        game.setAttempts(game.getAttempts() + 1);

        if (revealed.toString().equalsIgnoreCase(answer)) {
            game.setCorrect(true);
            game.setStatus(GameStatus.FINISHED);
        }
        var saved = gameHistoryRepository.save(game);
        return gameHistoryMapper.toResponse(saved);
    }

    @Transactional
    public GameHistoryResponse guessWord(Long gameId, String word) {
        GameHistory game = gameHistoryRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found"));
        if (game.getStatus().equals(GameStatus.FINISHED)) {
            throw new IllegalStateException("Game is already finished");
        }

        String answer = game.getQuestion().getAnswer().toLowerCase();
        String guess = word.trim();

        game.setAttempts(game.getAttempts() + 1);

        if (guess.equalsIgnoreCase(answer)) {
            game.setGuessedWord(answer);
            game.setCorrect(true);
            game.setStatus(GameStatus.FINISHED);
        }
        var saved = gameHistoryRepository.save(game);
        return gameHistoryMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<GameHistoryResponse> getAllUserGames(int page, int size) {
        User user = userService.getCurrentUser();

        Pageable pageable = PageRequest.of(page, size);
        Page<GameHistory> gamePage = gameHistoryRepository.findByUser(user, pageable);

        return gamePage.map(gameHistory -> gameHistoryMapper.toResponse(gameHistory));
    }
}
