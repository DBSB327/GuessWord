package com.pm.guessword.controller;

import com.pm.guessword.dto.QuestionRequest;
import com.pm.guessword.dto.QuestionResponse;
import com.pm.guessword.model.User;
import com.pm.guessword.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<QuestionResponse> createQuestion(@RequestBody @Valid QuestionRequest questionRequest) {
        QuestionResponse questionResponse = questionService.addQuestion(questionRequest);
        return ResponseEntity.ok(questionResponse);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<QuestionResponse>> getAllQuestions(@RequestParam int page, @RequestParam int size) {
        Page<QuestionResponse> questionResponses = questionService.getAllQuestions(page, size);
        return ResponseEntity.ok(questionResponses);
    }

    @PutMapping("/{questionId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<QuestionResponse> updateQuestion(@PathVariable Long questionId, @RequestBody @Valid QuestionRequest questionRequest) {
        QuestionResponse questionResponse = questionService.updateQuestion(questionId, questionRequest);
        return ResponseEntity.ok(questionResponse);
    }

    @DeleteMapping("/{questionId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long questionId) {
        questionService.deleteQuestion(questionId);
         return ResponseEntity.noContent().build();
    }
}
