package com.marketlogic.survey.service;

import com.marketlogic.survey.data.*;
import com.marketlogic.survey.exception.BadRequestException;
import com.marketlogic.survey.exception.ResourceNotFoundException;
import com.marketlogic.survey.model.*;
import com.marketlogic.survey.util.SurveyConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class SurveyService {

    private final SurveyQuestionRepository surveyQuestionRepository;
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final SurveyAnswerRepository surveyAnswerRepository;

    public SurveyService(SurveyQuestionRepository surveyQuestionRepository, VoteRepository voteRepository, UserRepository userRepository, SurveyAnswerRepository surveyAnswerRepository) {
        this.surveyQuestionRepository = surveyQuestionRepository;
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
        this.surveyAnswerRepository = surveyAnswerRepository;
    }

    public PagedResponse getAllSurveys(int page, int size) {
        log.debug("SurveyService.getAllSurveys");
        validatePageNumberAndSize(page, size);

        // Retrieve surveys
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<SurveyQuestion> surveys = surveyQuestionRepository.findAll(pageable);

        if (surveys.getNumberOfElements() == 0) {
            return PagedResponse.builder()
                    .content(Collections.emptyList())
                    .last(surveys.isLast())
                    .page(surveys.getNumber())
                    .size(surveys.getSize())
                    .totalElements(surveys.getTotalElements())
                    .totalPages(surveys.getTotalPages())
                    .build();
        }

        List<Long> surveyIds = surveys.map(SurveyQuestion::getId).getContent();
        Map<Long, User> creatorMap = getQuestionCreatorMap(surveys.getContent());

        List<SurveyView> surveyViewList = surveys.map(question -> {
            return ToSurveyViewTransformer.transformDaoToSurveyView(question,
                    creatorMap.get(question.getCreatedBy()),
                    Collections.emptyList());
        }).getContent();

        return PagedResponse.builder()
                .content(Collections.singletonList(surveyViewList))
                .last(surveys.isLast())
                .page(surveys.getNumber())
                .size(surveys.getSize())
                .totalElements(surveys.getTotalElements())
                .totalPages(surveys.getTotalPages())
                .build();
    }

    public List<String> getAllSurveysQuestions() {
        log.debug("SurveyService.getAllSurveysQuestions");
        return surveyQuestionRepository.findAllQuestions();
    }

    public QuestionResponse getQuestionDetails(Long surveyId) {
        log.debug("SurveyService.getQuestionDetails");
        SurveyQuestion survey = surveyQuestionRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found " + surveyId));

        List<AnswerResponse> answerResponses = new ArrayList<>();
        survey.getSurveyAnswers().stream()
                .forEach(answer -> {
                    answerResponses.add(AnswerResponse.builder()
                            .id(answer.getId())
                            .text(answer.getText())
                            .build());
                });

        return QuestionResponse.builder()
                .id(survey.getId())
                .answerResponses(answerResponses)
                .build();
    }


    public PagedResponse getUserSurveys(Long userId, int page, int size) {
        log.debug("SurveyService.getUserSurveys");
        validatePageNumberAndSize(page, size);


        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found " + userId));

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<SurveyQuestion> surveys = surveyQuestionRepository.findByCreatedBy(user.getId(), pageable);

        if (surveys.getNumberOfElements() == 0) {
            return PagedResponse.builder()
                    .content(Collections.emptyList())
                    .last(surveys.isLast())
                    .page(surveys.getNumber())
                    .size(surveys.getSize())
                    .totalElements(surveys.getTotalElements())
                    .totalPages(surveys.getTotalPages())
                    .build();
        }

        List<Long> questionIds = surveys.map(SurveyQuestion::getId).getContent();
        Map<Long, List<Long>> userVoteMap = getUserVoteMap(user.getId(), questionIds);

        List<SurveyView> surveyViewList = surveys.map(question -> {
            return ToSurveyViewTransformer.transformDaoToSurveyView(question,
                    user,
                    userVoteMap == null ? null : userVoteMap.getOrDefault(question.getId(), Collections.emptyList()));
        }).getContent();

        return PagedResponse.builder()
                .content(Collections.singletonList(surveyViewList))
                .last(surveys.isLast())
                .page(surveys.getNumber())
                .size(surveys.getSize())
                .totalElements(surveys.getTotalElements())
                .totalPages(surveys.getTotalPages())
                .build();
    }

    public PagedResponse getUserSurveyAnswers(Long userId, int page, int size) {
        log.debug("SurveyService.getUserSurveyAnswers");

        validatePageNumberAndSize(page, size);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found " + userId));

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Long> userVotedQuestionIds = voteRepository.findVotedQuestionIdsByUserId(user.getId(), pageable);

        if (userVotedQuestionIds.getNumberOfElements() == 0) {
            return PagedResponse.builder()
                    .content(Collections.emptyList())
                    .last(userVotedQuestionIds.isLast())
                    .page(userVotedQuestionIds.getNumber())
                    .size(userVotedQuestionIds.getSize())
                    .totalElements(userVotedQuestionIds.getTotalElements())
                    .totalPages(userVotedQuestionIds.getTotalPages())
                    .build();
        }

        List<Long> questionIds = userVotedQuestionIds.getContent();
        List<SurveyQuestion> surveyQuestions = surveyQuestionRepository.findByIdIn(questionIds, Sort.by(Sort.Direction.DESC, "createdAt"));

        Map<Long, List<Long>> userVoteMap = getUserVoteMap(user.getId(), questionIds);
        Map<Long, User> creatorMap = getQuestionCreatorMap(surveyQuestions);

        List<SurveyView> surveyViewList = surveyQuestions.stream().map(question -> {
            return ToSurveyViewTransformer.transformDaoToSurveyView(question,
                    creatorMap.get(question.getCreatedBy()),
                    userVoteMap == null ? null : userVoteMap.getOrDefault(question.getId(), Collections.emptyList()));
        }).collect(Collectors.toList());

        return PagedResponse.builder()
                .content(Collections.singletonList(surveyViewList))
                .last(userVotedQuestionIds.isLast())
                .page(userVotedQuestionIds.getNumber())
                .size(userVotedQuestionIds.getSize())
                .totalElements(userVotedQuestionIds.getTotalElements())
                .totalPages(userVotedQuestionIds.getTotalPages())
                .build();
    }

    public SurveyView addSurvey(Long userId, SurveyRequest surveyRequest) {
        log.debug("SurveyService.addSurvey");

        OffsetDateTime expirationDateTime = OffsetDateTime.now().plusDays(surveyRequest.getSurveyLength().getDays())
                .plusHours(surveyRequest.getSurveyLength().getHours());

        SurveyQuestion surveyQuestion = SurveyQuestion.builder()
                .question(surveyRequest.getQuestion())
                .expirationDateTime(expirationDateTime)
                .createdBy(userId)
                .build();

        List<SurveyAnswer> surveyAnswers = new ArrayList<>();
        SurveyQuestion finalSurveyQuestion = surveyQuestion;
        surveyRequest.getChoices().forEach(choiceRequest -> {
            surveyAnswers.add(SurveyAnswer.builder().text(choiceRequest.getText()).surveyQuestion(finalSurveyQuestion).build());
        });
        surveyQuestion.setSurveyAnswers(surveyAnswers);

        //Save survey
        surveyQuestion = surveyQuestionRepository.saveAndFlush(surveyQuestion);

        List<AnswerResponse> answerResponses = surveyQuestion.getSurveyAnswers().stream().map(surveyAnswer -> {
            return AnswerResponse.builder()
                    .id(surveyAnswer.getId())
                    .text(surveyAnswer.getText())
                    .build();
        }).collect(Collectors.toList());

        return SurveyView.builder()
                .id(surveyQuestion.getId())
                .question(surveyQuestion.getQuestion())
                .choices(answerResponses)
                .build();
    }

    @Transactional
    public SurveyView updateSurvey(Long userId, Long surveyId, SurveyRequest surveyRequest) {
        log.debug("SurveyService.updateSurvey");

        SurveyQuestion surveyQuestion = surveyQuestionRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found " + surveyId));

        List<Long> answerIds = surveyQuestion.getSurveyAnswers().stream()
                .map(SurveyAnswer::getId).collect(Collectors.toList());
        surveyAnswerRepository.deleteByAnswerIds(answerIds);

        OffsetDateTime expirationDateTime = OffsetDateTime.now().plusDays(surveyRequest.getSurveyLength().getDays())
                .plusHours(surveyRequest.getSurveyLength().getHours());

        surveyQuestion.setQuestion(surveyRequest.getQuestion());
        surveyQuestion.setUpdatedBy(userId);
        surveyQuestion.setExpirationDateTime(expirationDateTime);

        List<SurveyAnswer> surveyAnswers = new ArrayList<>();
        SurveyQuestion finalSurveyQuestion = surveyQuestion;
        surveyRequest.getChoices().forEach(choiceRequest -> {
            surveyAnswers.add(SurveyAnswer.builder().text(choiceRequest.getText()).surveyQuestion(finalSurveyQuestion).build());
        });
        finalSurveyQuestion.setSurveyAnswers(surveyAnswers);

        surveyQuestionRepository.saveAndFlush(finalSurveyQuestion);

        List<AnswerResponse> answerResponses = finalSurveyQuestion.getSurveyAnswers().stream().map(surveyAnswer -> {
            return AnswerResponse.builder()
                    .id(surveyAnswer.getId())
                    .text(surveyAnswer.getText())
                    .build();
        }).collect(Collectors.toList());

        return SurveyView.builder()
                .id(finalSurveyQuestion.getId())
                .question(finalSurveyQuestion.getQuestion())
                .choices(answerResponses)
                .build();
    }

    public boolean deleteSurvey(Long userId, Long surveyId) {
        log.debug("SurveyService.deleteSurvey");

        SurveyQuestion surveyQuestion = surveyQuestionRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey details not found for " + surveyId));
        surveyQuestionRepository.deleteById(surveyId);
        return true;
    }

    public SurveyView getSurveyDetailsById(Long surveyId) {
        log.debug("SurveyService.getSurveyDetailsById");

        SurveyQuestion surveyQuestion = surveyQuestionRepository.findById(surveyId).orElseThrow(
                () -> new ResourceNotFoundException("Survey not found"));

        User creator = userRepository.findById(surveyQuestion.getCreatedBy())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", surveyQuestion.getCreatedBy()));

        Vote userVote = null;
        List<Long> answers = null;

        if (surveyQuestion.getCreatedBy() != null) {
            userVote = voteRepository.findByUserIdAndQuestionId(surveyQuestion.getCreatedBy(), surveyId);
        }

        if (userVote != null) {
            answers = Stream.of(userVote.getSelectedAnswers().split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
        }
        List<AnswerResponse> answerResponses = new ArrayList<>();
        List<Long> finalAnswers = answers;
        surveyQuestion.getSurveyAnswers().stream().forEach(choice -> {
            if (finalAnswers!= null && finalAnswers.contains(choice.getId())) {
                AnswerResponse answerResponse = AnswerResponse.builder()
                        .id(choice.getId())
                        .text(choice.getText())
                        .build();
                answerResponses.add(answerResponse);
            }
        });

        long totalVotes = answerResponses.stream().mapToLong(AnswerResponse::getVoteCount).sum();

        return SurveyView.builder()
                .id(surveyQuestion.getId())
                .question(surveyQuestion.getQuestion())
                .creationDateTime(surveyQuestion.getCreatedAt())
                .expirationDateTime(surveyQuestion.getExpirationDateTime())
                .choices(answerResponses)
                .totalVotes(totalVotes)
                .build();
    }

    public SurveyView castAnswersAndGetUpdatedSurvey(Long userId, Long surveyId, @Valid VoteRequest voteRequest) {
        log.debug("SurveyService.castAnswersAndGetUpdatedSurvey");

        SurveyQuestion surveyQuestion = surveyQuestionRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", surveyId));

        if (surveyQuestion.getExpirationDateTime().isBefore(OffsetDateTime.now())) {
            throw new BadRequestException("Sorry! This survey has already expired");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found " + userId));

        String selectedAnswers = voteRequest.getAnswerIds().stream().map(String::valueOf)
                .collect(Collectors.joining(","));

//        SurveyAnswer selectedSurveyAnswer = surveyQuestion.getSurveyAnswers().stream()
//                .filter(surveyAnswer -> surveyAnswer.getId().equals(voteRequest.getAnswerId()))
//                .findFirst()
//                .orElseThrow(() -> new ResourceNotFoundException("Answer", "id", voteRequest.getAnswerId()));

        Vote vote = Vote.builder()
                .selectedAnswers(selectedAnswers)
                .surveyQuestion(surveyQuestion)
                .user(user)
                .build();

        try {
            vote = voteRepository.save(vote);
        } catch (DataIntegrityViolationException ex) {
            log.info("User {} has already voted in survey {}", userId, surveyId);
            throw new BadRequestException("Sorry! You have already cast your vote in this survey");
        }

//        List<SurveyAnswerVoteCount> votes = voteRepository.countByQuestionIdInGroupByAnswerId(surveyId);
//
//        Map<Long, Long> choiceVotesMap = votes.stream()
//                .collect(Collectors.toMap(SurveyAnswerVoteCount::getAnswerId, SurveyAnswerVoteCount::getVoteCount));

        List<Long> answers = Stream.of(vote.getSelectedAnswers().split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .collect(Collectors.toList());
        return ToSurveyViewTransformer.transformDaoToSurveyView(surveyQuestion, user, answers);
    }

    private void validatePageNumberAndSize(int page, int size) {
        if (page < 0) {
            throw new BadRequestException("Page number cannot be less than zero.");
        }

        if (size > SurveyConstants.MAX_PAGE_SIZE) {
            throw new BadRequestException("Page size must not be greater than " + SurveyConstants.MAX_PAGE_SIZE);
        }
    }

    private Map<Long, Long> getAnswerVoteCountMap(List<Long> questionIds) {
//        List<SurveyAnswerVoteCount> votes = voteRepository.countByQuestionIdsInGroupByAnswerId(questionIds);
//
//        Map<Long, Long> choiceVotesMap = votes.stream()
//                .collect(Collectors.toMap(SurveyAnswerVoteCount::getAnswerId, SurveyAnswerVoteCount::getVoteCount));

        return Collections.emptyMap();
    }

    private Map<Long, List<Long>> getUserVoteMap(Long userId, List<Long> questionIds) {
        Map<Long, List<Long>> userVoteMap = null;
        if (userId != null) {
            List<Vote> userVotes = voteRepository.findByUserIdAndQuestionIdIn(userId, questionIds);
            userVoteMap = new HashMap<>();
            Map<Long, List<Long>> finalUserVoteMap = userVoteMap;
            userVotes.stream().forEach(vote -> {
                String selectedAnswers = vote.getSelectedAnswers();
                List<Long> answers = Stream.of(selectedAnswers.split(","))
                        .map(String::trim)
                        .map(Long::parseLong)
                        .collect(Collectors.toList());

                finalUserVoteMap.put(vote.getSurveyQuestion().getId(), answers);
            });
//            userVoteMap = userVotes.stream()
//                    .collect(Collectors.toMap(vote -> vote.getSurveyQuestion().getId(), vote -> vote.getSelectedAnswers()));
        }
        return userVoteMap;
    }

    Map<Long, User> getQuestionCreatorMap(List<SurveyQuestion> surveyQuestions) {
        List<Long> creatorIds = surveyQuestions.stream()
                .map(SurveyQuestion::getCreatedBy)
                .distinct()
                .collect(Collectors.toList());

        List<User> creators = userRepository.findByIdIn(creatorIds);
        Map<Long, User> creatorMap = creators.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        return creatorMap;
    }
}
