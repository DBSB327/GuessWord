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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameHistoryService {

    private final GameHistoryRepository gameHistoryRepository;
    private final GameHistoryMapper gameHistoryMapper;
    private final QuestionRepository questionRepository;
    private final UserService userService;

    public GameHistoryResponse startGame() {
        User user = userService.getCurrentUser();
        Question question = questionRepository.findRandomQuestion();

        if(question == null) {
            throw new RuntimeException("Not available question");
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

    public GameHistoryResponse guessLetter(Long gameId, char letter) {
        GameHistory game = gameHistoryRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        if(game.getStatus().equals(GameStatus.FINISHED)) {
            throw new IllegalStateException("Game is already finished");
        }

        String answer = game.getQuestion().getAnswer().toLowerCase();
        String current = game.getGuessedWord();
        StringBuilder revealed = new StringBuilder(current);

        for(int i = 0; i < answer.length(); i++) {
            if(answer.charAt(i) == Character.toLowerCase(letter)) {
                revealed.setCharAt(i, answer.charAt(i));

            }
        }
        game.setGuessedWord(revealed.toString());
        game.setAttempts(game.getAttempts() + 1);

        if(revealed.toString().equalsIgnoreCase(answer)) {
            game.setCorrect(true);
            game.setStatus(GameStatus.FINISHED);
        }
        var saved =  gameHistoryRepository.save(game);
        return gameHistoryMapper.toResponse(saved);
    }

    public GameHistoryResponse guessWord(Long gameId, String word) {
        GameHistory game = gameHistoryRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));
        if(game.getStatus().equals(GameStatus.FINISHED)) {
            throw new IllegalStateException("Game is already finished");
        }

        String answer = game.getQuestion().getAnswer().toLowerCase();
        String guess = word.trim();

        game.setAttempts(game.getAttempts() + 1);

        if(guess.equalsIgnoreCase(answer)) {
            game.setGuessedWord(answer);
            game.setCorrect(true);
            game.setStatus(GameStatus.FINISHED);
        }
        var saved = gameHistoryRepository.save(game);
        return gameHistoryMapper.toResponse(saved);
    }

    public List<GameHistoryResponse> getAllUserGames (){
        User user = userService.getCurrentUser();
        List<GameHistory> gameHistories = gameHistoryRepository.findByUser(user);
        List<GameHistoryResponse> responses = new ArrayList<>();
        for(GameHistory gameHistory: gameHistories) {
            responses.add(gameHistoryMapper.toResponse(gameHistory));
        }
        return responses;
    }
}
