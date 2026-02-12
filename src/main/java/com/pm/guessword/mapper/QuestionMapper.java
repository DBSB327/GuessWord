package com.pm.guessword.mapper;

import com.pm.guessword.dto.QuestionAdminResponse;
import com.pm.guessword.dto.QuestionRequest;
import com.pm.guessword.dto.QuestionResponse;
import com.pm.guessword.model.Question;
import org.springframework.stereotype.Component;

@Component
public class QuestionMapper {


    public QuestionResponse toResponse(Question question){
        QuestionResponse response = new QuestionResponse();
        response.setId(question.getId());
        response.setQuestionText(question.getQuestionText());
        response.setCreatedAt(question.getCreatedAt());
        return response;
    }

    public Question toEntity(QuestionRequest questionRequest){
        Question question = new Question();
        question.setQuestionText(questionRequest.getQuestionText());
        question.setAnswer(questionRequest.getAnswer());
        return question;
    }

}
