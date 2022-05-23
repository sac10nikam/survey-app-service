package com.marketlogic.survey.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface SurveyAnswerRepository extends JpaRepository<SurveyAnswer, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM SurveyAnswer a WHERE a.id IN :answerIds")
    void deleteByAnswerIds(@Param("answerIds") List<Long> answerIds);
}

