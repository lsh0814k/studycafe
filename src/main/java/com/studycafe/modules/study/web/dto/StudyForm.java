package com.studycafe.modules.study.web.dto;

import com.studycafe.modules.study.domain.Study;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter @Setter
public class StudyForm {
    @NotNull
    @Length(min = 2, max = 20)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{2,20}$")
    private String path;

    @NotBlank
    @Length(max = 50)
    private String title;

    @NotBlank
    @Length(max = 100)
    private String shortDescription;

    @NotBlank
    private String fullDescription;


    public Study createStudy() {
        return Study.builder()
                .path(path)
                .title(title)
                .shortDescription(shortDescription)
                .fullDescription(fullDescription)
                .build();
    }
}