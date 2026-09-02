package com.toucan.transaction.dto;

import com.toucan.transaction.entity.Transaction;
import com.toucan.transaction.model.TransactionStatus;
import com.toucan.transaction.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {

    private String transactionId;
    private String customerId;
    private BigDecimal amount;
    private String currency;
    private TransactionType transactionType;
    private TransactionStatus transactionStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TransactionResponse() {
    }

    public TransactionResponse(String transactionId, String customerId, BigDecimal amount, String currency,
                               TransactionType transactionType, TransactionStatus transactionStatus,
                               LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.transactionId = transactionId;
        this.customerId = customerId;
        this.amount = amount;
        this.currency = currency;
        this.transactionType = transactionType;
        this.transactionStatus = transactionStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static TransactionResponse fromEntity(Transaction entity) {
        if (entity == null) return null;
        return TransactionResponse.builder()
                .transactionId(entity.getTransactionId())
                .customerId(entity.getCustomerId())
                .amount(entity.getAmount())
                .currency(entity.getCurrency())
                .transactionType(entity.getTransactionType())
                .transactionStatus(entity.getTransactionStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // Getters and Setters
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public TransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(TransactionType transactionType) { this.transactionType = transactionType; }

    public TransactionStatus getTransactionStatus() { return transactionStatus; }
    public void setTransactionStatus(TransactionStatus transactionStatus) { this.transactionStatus = transactionStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Builder pattern
    public static TransactionResponseBuilder builder() {
        return new TransactionResponseBuilder();
    }

    public static class TransactionResponseBuilder {
        private String transactionId;
        private String customerId;
        private BigDecimal amount;
        private String currency;
        private TransactionType transactionType;
        private TransactionStatus transactionStatus;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public TransactionResponseBuilder transactionId(String transactionId) { this.transactionId = transactionId; return this; }
        public TransactionResponseBuilder customerId(String customerId) { this.customerId = customerId; return this; }
        public TransactionResponseBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
        public TransactionResponseBuilder currency(String currency) { this.currency = currency; return this; }
        public TransactionResponseBuilder transactionType(TransactionType transactionType) { this.transactionType = transactionType; return this; }
        public TransactionResponseBuilder transactionStatus(TransactionStatus transactionStatus) { this.transactionStatus = transactionStatus; return this; }
        public TransactionResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public TransactionResponseBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public TransactionResponse build() {
            return new TransactionResponse(transactionId, customerId, amount, currency, transactionType, transactionStatus, createdAt, updatedAt);
        }
    }
}
