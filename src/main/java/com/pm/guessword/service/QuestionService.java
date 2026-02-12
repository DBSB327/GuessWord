package com.pm.guessword.service;

import com.pm.guessword.dto.QuestionRequest;
import com.pm.guessword.dto.QuestionResponse;
import com.pm.guessword.mapper.QuestionMapper;
import com.pm.guessword.model.Question;
import com.pm.guessword.model.User;
import com.pm.guessword.repository.QuestionRepository;
import com.pm.guessword.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;
    private final UserService userService;

    public QuestionResponse addQuestion(QuestionRequest questionRequest ) {
        User currentUser = userService.getCurrentUser();
        Question question = questionMapper.toEntity(questionRequest);
        question.setCreatedBy(currentUser);
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
