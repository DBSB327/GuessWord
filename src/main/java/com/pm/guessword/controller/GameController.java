package com.pm.guessword.controller;

import com.pm.guessword.dto.GameHistoryResponse;
import com.pm.guessword.service.GameHistoryService;
import com.pm.guessword.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GameHistoryService gameHistoryService;

    @PostMapping("/start/{userId}")
    public ResponseEntity<GameHistoryResponse> startGame(@PathVariable Long userId){
        GameHistoryResponse gameHistoryResponse = gameHistoryService.startGame(userId);
        return ResponseEntity.ok(gameHistoryResponse);
    }

    @PostMapping("/{gameId}/guess/letter")
    public ResponseEntity<GameHistoryResponse> guessLetter(@PathVariable Long gameId, @RequestParam char letter){
        GameHistoryResponse gameHistoryResponse = gameHistoryService.guessLetter(gameId, letter);
        return ResponseEntity.ok(gameHistoryResponse);
    }

    @PostMapping("/{gameId}/guess/word")
    public ResponseEntity<GameHistoryResponse> guessWord(@PathVariable Long gameId, @RequestParam String word){
        GameHistoryResponse gameHistoryResponse = gameHistoryService.guessWord(gameId, word);
        return ResponseEntity.ok(gameHistoryResponse);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GameHistoryResponse>> getUserGames(@PathVariable Long userId){
        List<GameHistoryResponse> gameHistoryResponses = gameHistoryService.getAllUserGames(userId);
        return ResponseEntity.ok(gameHistoryResponses);
    }
}
