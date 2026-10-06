package com.example.democi;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "visit_log")
public class VisitLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_ip", nullable = false, length = 64)
    private String clientIp;

    @Column(name = "accessed_at", nullable = false)
    private Instant accessedAt;

    protected VisitLog() {
    }

    public VisitLog(String clientIp, Instant accessedAt) {
        this.clientIp = clientIp;
        this.accessedAt = accessedAt;
    }
}
