package com.marketlogic.survey.service;

import com.marketlogic.survey.data.SurveyQuestionRepository;
import com.marketlogic.survey.data.User;
import com.marketlogic.survey.data.UserRepository;
import com.marketlogic.survey.data.VoteRepository;
import com.marketlogic.survey.exception.ResourceNotFoundException;
import com.marketlogic.survey.model.UserView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    private final SurveyQuestionRepository surveyQuestionRepository;
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;

    public UserService(SurveyQuestionRepository surveyQuestionRepository, VoteRepository voteRepository, UserRepository userRepository) {
        this.surveyQuestionRepository = surveyQuestionRepository;
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
    }

    public UserView getUserSurveyDetails(Long userId) {
        log.debug("UserService.getUserSurveyDetails");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found " + userId));

        long surveyCount = surveyQuestionRepository.countByCreatedBy(user.getId());
        long voteCount = voteRepository.countByUserId(user.getId());

        return UserView.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .surveyCount(surveyCount)
                .voteCount(voteCount)
                .build();
    }

    public boolean existsByUsername(String username) {
        return !userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return !userRepository.existsByEmail(email);
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found " + userId));

    }
}
