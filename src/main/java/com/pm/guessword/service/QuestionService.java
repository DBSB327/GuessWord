package com.pm.guessword.service;

import com.pm.guessword.dto.QuestionRequest;
import com.pm.guessword.dto.QuestionResponse;
import com.pm.guessword.exception.QuestionNotFoundException;
import com.pm.guessword.mapper.QuestionMapper;
import com.pm.guessword.model.Question;
import com.pm.guessword.model.User;
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

        log.info("User {} added a new question {}", currentUser.getUsername(), saved.getId());

        return questionMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<QuestionResponse> getAllQuestions(String text, int page, int size, String sortBy, String direction) {

        Sort.Direction dir = Sort.Direction.fromString(direction);

        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sortBy));
        Page<Question> questions;

        if(text != null && !text.isEmpty()) {
            questions = questionRepository.findByQuestionTextContainingIgnoreCase(text, pageable);
        } else {
            questions = questionRepository.findAll(pageable);
        }

        log.info("Retrieved page {} of questions, size {}", page, size);

        return questions.map(questionMapper::toResponse);
    }

    @Transactional
    public QuestionResponse updateQuestion(Long questionId, QuestionRequest questionRequest) {
        Question question = questionRepository.findById(questionId).
                orElseThrow(() -> new QuestionNotFoundException(questionId));
        question.setQuestionText(questionRequest.getQuestionText());
        question.setAnswer(questionRequest.getAnswer());
        var saved = questionRepository.save(question);

        log.info("Updated question id {}: new text '{}'", questionId, question.getQuestionText());

        return questionMapper.toResponse(saved);
    }

    @Transactional
    public void deleteQuestion(Long questionId) {
        if(!questionRepository.existsById(questionId)){
            throw new QuestionNotFoundException(questionId);
        }
        questionRepository.deleteById(questionId);

        log.info("Deleted question id {}", questionId);
    }
}
