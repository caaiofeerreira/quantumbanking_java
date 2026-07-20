package com.quantumbanking.modules.transaction.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "TransactionOutbox")
@Table(name = "tb_transaction_outbox")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class TransactionOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false, unique = true)
    private Transaction transaction;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OutboxStatus status;

    @Column(name = "retry_count", nullable = false)
    @Builder.Default
    private Integer retryCount = 0;

    @Column(name = "stream_message_id")
    private String streamMessageId;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.status = OutboxStatus.PENDING_PUBLISH;
    }

    public void markPublished(String streamMessageId) {
        this.status = OutboxStatus.PUBLISHED;
        this.streamMessageId = streamMessageId;
        this.publishedAt = LocalDateTime.now();
    }

    public void registerFailedAttempt(String errorMessage, int maxRetryAttempts) {
        this.retryCount++;
        this.errorMessage = errorMessage;

        if (this.retryCount >= maxRetryAttempts) {
            this.status = OutboxStatus.FAILED;
        }
    }
}