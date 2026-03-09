package com.pm.guessword.service;

import com.pm.guessword.dto.GameHistoryResponse;
import com.pm.guessword.enums.GameStatus;
import com.pm.guessword.exception.GameNotFoundException;
import com.pm.guessword.exception.NoQuestionsAvailableException;
import com.pm.guessword.mapper.GameHistoryMapper;
import com.pm.guessword.model.GameHistory;
import com.pm.guessword.model.Question;
import com.pm.guessword.model.User;
import com.pm.guessword.repository.GameHistoryRepository;
import com.pm.guessword.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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
        log.info("User {} started a new game", user.getUsername());

        Question question = questionRepository.findRandomQuestion();

        if (question == null) {
            throw new NoQuestionsAvailableException();
        }

        GameHistory game = new GameHistory();
        game.setUser(user);
        game.setQuestion(question);
        game.setGuessedWord("_".repeat(question.getAnswer().length()));
        game.setCorrect(false);
        game.setAttempts(0);
        game.setStatus(GameStatus.IN_PROGRESS);
        game.setMaxAttempts(question.getAnswer().length() + 5);
        game.setRemainingWordGuesses(2);
        game.setEarnedScore(0);

        GameHistory savedGame = gameHistoryRepository.save(game);

        log.info("Game {} started for user {}",  savedGame.getId(), user.getUsername());

        return gameHistoryMapper.toResponse(savedGame);
    }

    @Transactional
    public GameHistoryResponse guessLetter(Long gameId, char letter) {
        GameHistory game = getInProgressGame(gameId);

        letter = Character.toLowerCase(letter);

        if (game.getGuessedWord().toLowerCase().contains(String.valueOf(letter))) {
            throw new IllegalArgumentException("Letter already guessed");
        };

        int lettersFound = revealLetters(game, letter);
        updateScoreForLetter(game, lettersFound);
        game.setAttempts(game.getAttempts() + 1);

        log.info("User {} guessed letter '{}' in game {}", game.getUser().getUsername(), letter, gameId);

        finishGameIfNeeded(game);
        return gameHistoryMapper.toResponse(gameHistoryRepository.save(game));
    }

    @Transactional
    public GameHistoryResponse guessWord(Long gameId, String word) {
        GameHistory game = getInProgressGame(gameId);

        String answer = game.getQuestion().getAnswer().toLowerCase();
        String guess = word.trim().toLowerCase();

        log.debug("User {} attempts to guess word '{}' in game {}", game.getUser().getUsername(), word, game.getGuessedWord());

        if (guess.equals(answer)) {
            addScoreForRemainingLetters(game);
            game.setGuessedWord(game.getQuestion().getAnswer());
            game.setCorrect(true);
            game.setStatus(GameStatus.FINISHED_WIN);
            updateUserTotalScore(game);

            log.info("User {} finished. User {} won with score {}", gameId, game.getUser().getUsername(), game.getEarnedScore());

        } else {
            game.setRemainingWordGuesses(game.getRemainingWordGuesses() - 1);
            if (game.getRemainingWordGuesses() <= 0) {
                game.setStatus(GameStatus.FINISHED_LOSE);
                game.setEarnedScore(0);

                log.info("Game {} finished. User {} lost", gameId, game.getUser().getUsername());
            }
        }

        game.setAttempts(game.getAttempts() + 1);

        return gameHistoryMapper.toResponse(gameHistoryRepository.save(game));
    }

    @Transactional(readOnly = true)
    public Page<GameHistoryResponse> getAllUserGames(int page, int size, String sortBy, String direction, GameStatus statusFilter) {
        User user = userService.getCurrentUser();
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<GameHistory> gamePage;

        if(statusFilter != null) {
            gamePage = gameHistoryRepository.findByUserAndStatus(user, statusFilter, pageable);
        }
        else{
            gamePage = gameHistoryRepository.findByUser(user, pageable);

        }
        log.info("User {} requested game history page {}, size {}, sorted by {} {}, returned {} records, filter status {}",
        user.getUsername(), page, size, sortBy, direction, gamePage.getNumberOfElements(), statusFilter);

        return gamePage.map(gameHistoryMapper::toResponse);
    }



    private GameHistory getInProgressGame(Long gameId) {
        GameHistory game = gameHistoryRepository.findById(gameId)
                .orElseThrow(() ->
                        new GameNotFoundException(gameId));

        if (game.getStatus() != GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("Game is already finished");
        }
        return game;
    }

    private int revealLetters(GameHistory game, char letter) {
        String answer = game.getQuestion().getAnswer().toLowerCase();
        StringBuilder revealed = new StringBuilder(game.getGuessedWord());
        int count = 0;

        for (int i = 0; i < answer.length(); i++) {
            if (answer.charAt(i) == letter) {
                revealed.setCharAt(i, game.getQuestion().getAnswer().charAt(i));
                count++;
            }
        }

        game.setGuessedWord(revealed.toString());
        return count;
    }

    private void updateScoreForLetter(GameHistory game, int lettersFound) {
        if (lettersFound > 0) {
            game.setEarnedScore(game.getEarnedScore() + lettersFound * 10);
        } else {
            game.setEarnedScore(Math.max(game.getEarnedScore() - 10, 0));
        }
    }

    private void addScoreForRemainingLetters(GameHistory game) {
        String guessed = game.getGuessedWord();
        String answer = game.getQuestion().getAnswer();
        int remaining = 0;

        for (int i = 0; i < answer.length(); i++) {
            if (guessed.charAt(i) == '_') remaining++;
        }

        game.setEarnedScore(game.getEarnedScore() + remaining * 10);
    }

    private void finishGameIfNeeded(GameHistory game) {
        String answer = game.getQuestion().getAnswer();
        String guessed = game.getGuessedWord();

        if (guessed.equalsIgnoreCase(answer)) {
            game.setCorrect(true);
            game.setStatus(GameStatus.FINISHED_WIN);
            updateUserTotalScore(game);
        } else if (game.getAttempts() >= game.getMaxAttempts()) {
            game.setStatus(GameStatus.FINISHED_LOSE);
            game.setEarnedScore(0);
        }
    }

    private void updateUserTotalScore(GameHistory game) {
        User user = game.getUser();
        user.setTotalScore(user.getTotalScore() + game.getEarnedScore());
        log.info("User {} total score updated to {}", user.getUsername(), user.getTotalScore());
    }
}