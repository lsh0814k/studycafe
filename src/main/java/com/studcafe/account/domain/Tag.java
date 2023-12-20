package com.studcafe.account.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import static lombok.AccessLevel.*;

@Entity @Getter @Setter @EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = PROTECTED)
public class Tag {
    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String title;
}
