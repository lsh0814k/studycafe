package com.studcafe.study.domain;

import com.studcafe.account.domain.Account;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter @NoArgsConstructor(access = PROTECTED) @EqualsAndHashCode(of = "id")
@AllArgsConstructor @Builder
public class StudyMember {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

}
