package com.studycafe.modules.study.domain;

import com.studycafe.modules.account.domain.Account;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter @EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor
@Builder
public class StudyManager {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="account_id")
    private Account account;
}
