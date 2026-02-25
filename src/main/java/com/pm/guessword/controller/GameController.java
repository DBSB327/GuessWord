package com.pm.guessword.controller;

import com.pm.guessword.dto.GameHistoryResponse;
import com.pm.guessword.service.GameHistoryService;
import com.pm.guessword.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GameHistoryService gameHistoryService;

    @PostMapping("/start")
    public ResponseEntity<GameHistoryResponse> startGame(){
        GameHistoryResponse gameHistoryResponse = gameHistoryService.startGame();
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

    @GetMapping("/user")
    public ResponseEntity<Page<GameHistoryResponse>> getUserGames(@RequestParam int page, @RequestParam int size){
        Page<GameHistoryResponse> gameHistoryResponses = gameHistoryService.getAllUserGames(page, size);
        return ResponseEntity.ok(gameHistoryResponses);
    }
}
