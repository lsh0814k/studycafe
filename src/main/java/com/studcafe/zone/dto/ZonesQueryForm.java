package com.studcafe.zone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder @AllArgsConstructor
public class ZonesQueryForm {
    private Long id;
    private String localNameOfCity;
}
