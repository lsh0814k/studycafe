package com.studcafe.security;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity @Table(name = "persistent_logins")
@Getter
public class PersistentLogins {
    @Id @Column(length = 64)
    private String series;

    @Column(nullable = false, length = 64)
    private String username;

    @Column(nullable = false, length = 64)
    private String token;

    @Column(name ="last_used", nullable = false, length = 64)
    private LocalDateTime lastUsed;
}
