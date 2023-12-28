package com.studycafe.modules.zone.repository;

import com.studycafe.modules.zone.domain.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface ZoneRepository extends JpaRepository<Zone, Long> {
    Optional<Zone> findByCityAndProvince(String cityName, String provinceName);
}
