package com.studycafe.modules.study.web.dto;

import com.studycafe.modules.study.domain.Study;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter @Setter @NoArgsConstructor
public class StudyDescriptionForm {
    @NotBlank
    @Length(max = 100)
    private String fullDescription;
    @NotBlank
    private String shortDescription;

    public StudyDescriptionForm(Study study) {
        shortDescription = study.getShortDescription();
        fullDescription = study.getFullDescription();
    }

    public Study createStudy() {
        return Study.builder()
                .fullDescription(fullDescription)
                .shortDescription(shortDescription)
                .build();
    }
}
