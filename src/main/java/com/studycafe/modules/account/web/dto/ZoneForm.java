package com.studycafe.modules.account.web.dto;

import com.studycafe.modules.zone.domain.Zone;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter
@NoArgsConstructor
public class ZoneForm {
    private String zoneName;

    public String getCityName() {
        return zoneName.substring(0, zoneName.indexOf("("));
    }

    public String getProvinceName() {
        return zoneName.substring(zoneName.indexOf("/") + 1);
    }

    public String getLocalNameOfCity() {
        return zoneName.substring(zoneName.indexOf("(") + 1, zoneName.indexOf(")"));
    }

    public Zone getZone() {
        return Zone.builder()
                .city(getCityName())
                .localNameOfCity(getLocalNameOfCity())
                .province(getProvinceName())
                .build();
    }
}
