package org.sid.ebankingbackend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;
    private String username;
    private String details;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime timestamp;

    public AuditLog() {}

    public AuditLog(String action, String username, String details) {
        this.action = action;
        this.username = username;
        this.details = details;
    }

}
