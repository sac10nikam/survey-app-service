package com.marketlogic.survey.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SurveyQuestionRepository extends JpaRepository<SurveyQuestion, Long> {

    Optional<SurveyQuestion> findById(Long questionId);

    Page<SurveyQuestion> findByCreatedBy(Long userId, Pageable pageable);

    long countByCreatedBy(Long userId);

    List<SurveyQuestion> findByIdIn(List<Long> questionIds, Sort sort);

    @Query("Select a.question FROM SurveyQuestion a")
    List<String> findAllQuestions();
}
