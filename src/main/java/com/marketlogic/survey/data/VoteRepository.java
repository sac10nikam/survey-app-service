package com.marketlogic.survey.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    @Query("SELECT v FROM Vote v where v.user.id = :userId and v.surveyQuestion.id in :questionIds")
    List<Vote> findByUserIdAndQuestionIdIn(@Param("userId") Long userId, @Param("questionIds") List<Long> questionIds);

    @Query("SELECT v FROM Vote v where v.user.id = :userId and v.surveyQuestion.id = :questionId")
    Vote findByUserIdAndQuestionId(@Param("userId") Long userId, @Param("questionId") Long questionId);

    @Query("SELECT COUNT(v.id) from Vote v where v.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    @Query("SELECT v.surveyQuestion.id FROM Vote v WHERE v.user.id = :userId")
    Page<Long> findVotedQuestionIdsByUserId(@Param("userId") Long userId, Pageable pageable);
}