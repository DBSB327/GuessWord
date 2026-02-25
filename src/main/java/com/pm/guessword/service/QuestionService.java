package com.pm.guessword.service;

import com.pm.guessword.dto.QuestionRequest;
import com.pm.guessword.dto.QuestionResponse;
import com.pm.guessword.mapper.QuestionMapper;
import com.pm.guessword.model.Question;
import com.pm.guessword.model.User;
import com.pm.guessword.repository.QuestionRepository;
import com.pm.guessword.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;
    private final UserService userService;

    @Transactional
    public QuestionResponse addQuestion(QuestionRequest questionRequest ) {
        User currentUser = userService.getCurrentUser();
        Question question = questionMapper.toEntity(questionRequest);
        question.setCreatedBy(currentUser);
        var saved =  questionRepository.save(question);
        return questionMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<QuestionResponse> getAllQuestions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Question> questionPage = questionRepository.findAll(pageable);

        return questionPage.map(question -> questionMapper.toResponse(question));
    }

    @Transactional
    public QuestionResponse updateQuestion(Long questionId, QuestionRequest questionRequest) {
        Question question = questionRepository.findById(questionId).
                orElseThrow(() -> new EntityNotFoundException("Question not found"));
        question.setQuestionText(questionRequest.getQuestionText());
        question.setAnswer(questionRequest.getAnswer());
        var saved = questionRepository.save(question);
        return questionMapper.toResponse(saved);
    }

    @Transactional
    public void deleteQuestion(Long id) {
        if(!questionRepository.existsById(id)){
            throw new EntityNotFoundException("Question with id " + id + " not found");
        }
        questionRepository.deleteById(id);
    }
}
