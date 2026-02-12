package com.pm.guessword.mapper;

import com.pm.guessword.dto.GameHistoryResponse;
import com.pm.guessword.model.GameHistory;
import org.springframework.stereotype.Component;

@Component
public class GameHistoryMapper {

    public GameHistoryResponse toResponse(GameHistory game) {
        GameHistoryResponse response = new GameHistoryResponse();
        response.setId(game.getId());
        response.setQuestionText(game.getQuestion().getQuestionText());
        response.setGuessedWord(game.getGuessedWord());
        response.setCorrect(game.isCorrect());
        response.setAttempts(game.getAttempts());
        response.setDate(game.getDate());
        return response;
    }

}
