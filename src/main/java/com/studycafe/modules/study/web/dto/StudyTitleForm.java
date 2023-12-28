package com.studycafe.modules.study.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter @Setter @NoArgsConstructor
@AllArgsConstructor @Builder
public class StudyTitleForm {
    @NotBlank
    @Length(max = 50)
    private String newTitle;
}
