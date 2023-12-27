package com.studcafe.event.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor
@AllArgsConstructor @Builder
public class EventEventEditForm {
    private Long id;
    private String title;
}
