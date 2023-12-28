package com.studycafe.modules.study.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter @Setter @NoArgsConstructor
@AllArgsConstructor @Builder
public class StudyPathForm {
    @NotNull
    @Length(min = 2, max = 20)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{2,20}$")
    private String newPath;
}
