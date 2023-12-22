package com.studcafe.study.domain;

import com.studcafe.account.domain.Account;
import com.studcafe.security.UserAccount;
import com.studcafe.tag.domain.Tag;
import com.studcafe.zone.domain.Zone;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.EAGER;
import static lombok.AccessLevel.PROTECTED;

@Entity @Getter
@AllArgsConstructor @Builder
@NoArgsConstructor(access = PROTECTED) @EqualsAndHashCode(of = "id")
public class Study {
    @Id @GeneratedValue
    @Column(name = "study_id")
    private Long id;

    @Builder.Default
    @OneToMany(mappedBy = "study", cascade = ALL, orphanRemoval = true)
    private Set<StudyManager> managers = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "study", cascade = ALL, orphanRemoval = true)
    private Set<StudyMember> members = new HashSet<>();

    @Builder.Default
    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    @Builder.Default
    @ManyToMany
    private Set<Zone> zones = new HashSet<>();

    @Column(unique = true)
    private String path;

    private String title;

    private String shortDescription;

    @Lob @Basic(fetch = EAGER)
    private String fullDescription;

    @Lob @Basic(fetch = EAGER)
    private String image;

    private LocalDateTime publishedDateTime;

    private LocalDateTime closedDateTime;

    private LocalDateTime recruitingUpdatedDateTime;

    private boolean recruiting;

    private boolean published;

    private boolean closed;

    private boolean useBanner;

    public void addManger(Account account) {
        StudyManager studyManager = StudyManager.builder()
                .study(this)
                .account(account)
                .build();

        managers.add(studyManager);
    }
}
