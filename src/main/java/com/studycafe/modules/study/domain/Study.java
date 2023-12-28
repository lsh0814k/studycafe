package com.studycafe.modules.study.domain;

import com.studycafe.modules.account.domain.Account;
import com.studycafe.modules.tag.domain.Tag;
import com.studycafe.modules.zone.domain.Zone;
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

    public void updateDescription(Study study) {
        this.fullDescription = study.getFullDescription();
        this.shortDescription = study.getShortDescription();
    }

    public boolean isManagerOf(Account account) {
        return managers.stream().map(StudyManager::getAccount).filter(a -> a.equals(account)).count() == 1;
    }

    public void updateUseBanner(boolean useBanner) {
        this.useBanner = useBanner;
    }

    public void updateBanner(String image) {
        this.image = image;
    }

    public void publish() {
        if (!this.closed && !this.published) {
            this.published = true;
            this.publishedDateTime = LocalDateTime.now();
        } else {
            throw new IllegalStateException("스터디를 공개할 수 없는 상태입니다. 스터디를 이미 공개했거나 종료했습니다.");
        }
    }

    public void close() {
        if (this.published && !this.closed) {
            this.closed = true;
            this.closedDateTime = LocalDateTime.now();
        } else {
            throw new IllegalStateException("스터디를 종료할 수 없습니다. 스터디를 공개하지 않았거나 이미 종료한 스터디입니다.");
        }
    }

    public void updateStudyTitle(String title) {
        this.title = title;
    }

    public void updateStudyPath(String path) {
        this.path = path;
    }

    public Boolean isRemovable() {
        // TODO 모임을 했던 스터디는 삭제할 수 없다.
        return !this.published;
    }

    public void removeMember(Account account) {
        StudyMember studyMember = this.members.stream()
                .filter(m -> m.getAccount().equals(account))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("스터디 멤버가 아닙니다."));
        this.members.remove(studyMember);
    }

    public void addMember(Account account) {
        this.members.add(StudyMember.builder().study(this).account(account).build());
    }

    public boolean canUpdateRecruiting() {
        return this.published;
    }

    public void startRecruit() {
        if (canUpdateRecruiting()) {
         this.recruiting = true;
         this.recruitingUpdatedDateTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("인원 모집을 시작할 수 없습니다. 먼저 스터디를 공개해주세요.");
        }
    }

    public void stopRecruit() {
        if (canUpdateRecruiting()) {
            this.recruiting = false;
            this.recruitingUpdatedDateTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("인원 모집을 시작할 수 없습니다. 먼저 스터디를 공개해주세요.");
        }
    }
}
