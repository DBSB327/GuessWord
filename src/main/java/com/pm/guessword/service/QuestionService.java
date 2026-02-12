package com.pm.guessword.service;

import com.pm.guessword.dto.QuestionRequest;
import com.pm.guessword.dto.QuestionResponse;
import com.pm.guessword.mapper.QuestionMapper;
import com.pm.guessword.model.Question;
import com.pm.guessword.model.User;
import com.pm.guessword.repository.QuestionRepository;
import com.pm.guessword.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.expression.spel.ast.QualifiedIdentifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;
    private final UserRepository userRepository;

    public QuestionResponse addQuestion(QuestionRequest questionRequest, Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        Question question = questionMapper.toEntity(questionRequest);
        question.setCreatedBy(admin);
        var saved =  questionRepository.save(question);
        return questionMapper.toResponse(saved);
    }

    public List<QuestionResponse> getAllQuestions() {
        List<Question> questions = questionRepository.findAll();
        List<QuestionResponse> responses = new ArrayList<>();
        for(Question question : questions) {
            responses.add(questionMapper.toResponse(question));
        }
        return responses;
    }

    public QuestionResponse updateQuestion(Long questionId, QuestionRequest questionRequest) {
        Question question = questionRepository.findById(questionId).
                orElseThrow(() -> new RuntimeException("Question not found"));
        question.setQuestionText(questionRequest.getQuestionText());
        question.setAnswer(questionRequest.getAnswer());
        var saved = questionRepository.save(question);
        return questionMapper.toResponse(saved);
    }

    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }

}
