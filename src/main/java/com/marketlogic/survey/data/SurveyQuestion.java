package com.marketlogic.survey.data;

import com.marketlogic.survey.data.audit.UserDateAudit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SurveyQuestion extends UserDateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 140)
    private String question;

    @OneToMany(
            mappedBy = "surveyQuestion",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
            //orphanRemoval = true
    )
    @Size(min = 2, max = 6)
    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 30)
    private List<SurveyAnswer> surveyAnswers = new ArrayList<>();

    @NotNull
    private OffsetDateTime expirationDateTime;

    @CreatedBy
    private Long createdBy;

    @LastModifiedBy
    private Long updatedBy;

}
