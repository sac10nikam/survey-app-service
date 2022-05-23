package com.marketlogic.survey.data;

import com.marketlogic.survey.data.audit.DateAudit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "votes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "question_id",
                "user_id"
        })
})
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Vote extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private SurveyQuestion surveyQuestion;

    //    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    //@JoinColumn(name = "answers", nullable = false)
    @Column(name = "answers", nullable = false)
    private String selectedAnswers;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
