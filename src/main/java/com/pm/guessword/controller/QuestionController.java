package com.pm.guessword.controller;

import com.pm.guessword.dto.QuestionRequest;
import com.pm.guessword.dto.QuestionResponse;
import com.pm.guessword.model.User;
import com.pm.guessword.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/{adminId}")
    public ResponseEntity<QuestionResponse> createQuestion(@RequestBody QuestionRequest questionRequest, @RequestParam Long adminId) {
        QuestionResponse questionResponse = questionService.addQuestion(questionRequest, adminId);
        return ResponseEntity.ok(questionResponse);
    }

    @GetMapping
    public ResponseEntity<List<QuestionResponse>> getAllQuestions() {
        List<QuestionResponse> questionResponses = questionService.getAllQuestions();
        return ResponseEntity.ok(questionResponses);
    }

    @PutMapping("/{questionId}")
    public ResponseEntity<QuestionResponse> updateQuestion(@PathVariable Long questionId, @RequestBody QuestionRequest questionRequest) {
        QuestionResponse questionResponse = questionService.updateQuestion(questionId, questionRequest);
        return ResponseEntity.ok(questionResponse);
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long questionId) {
        questionService.deleteQuestion(questionId);
         return ResponseEntity.noContent().build();
    }
}
