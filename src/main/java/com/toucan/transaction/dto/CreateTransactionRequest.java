package com.toucan.transaction.dto;

import com.toucan.transaction.model.TransactionStatus;
import com.toucan.transaction.model.TransactionType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class CreateTransactionRequest {

    @NotBlank(message = "Customer ID is required and must not be blank")
    @Size(min = 3, max = 64, message = "Customer ID must be between 3 and 64 characters")
    private String customerId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be strictly greater than 0.00")
    @Digits(integer = 15, fraction = 4, message = "Amount format is invalid (max 15 integer digits, 4 fraction digits)")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a valid 3-letter ISO 4217 code (e.g., USD, EUR, GBP)")
    private String currency;

    @NotNull(message = "Transaction type is required")
    private TransactionType transactionType;

    private TransactionStatus initialStatus;

    public CreateTransactionRequest() {
    }

    public CreateTransactionRequest(String customerId, BigDecimal amount, String currency, TransactionType transactionType, TransactionStatus initialStatus) {
        this.customerId = customerId;
        this.amount = amount;
        this.currency = currency;
        this.transactionType = transactionType;
        this.initialStatus = initialStatus;
    }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public TransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(TransactionType transactionType) { this.transactionType = transactionType; }

    public TransactionStatus getInitialStatus() { return initialStatus; }
    public void setInitialStatus(TransactionStatus initialStatus) { this.initialStatus = initialStatus; }

    public static CreateTransactionRequestBuilder builder() {
        return new CreateTransactionRequestBuilder();
    }

    public static class CreateTransactionRequestBuilder {
        private String customerId;
        private BigDecimal amount;
        private String currency;
        private TransactionType transactionType;
        private TransactionStatus initialStatus;

        public CreateTransactionRequestBuilder customerId(String customerId) { this.customerId = customerId; return this; }
        public CreateTransactionRequestBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
        public CreateTransactionRequestBuilder currency(String currency) { this.currency = currency; return this; }
        public CreateTransactionRequestBuilder transactionType(TransactionType transactionType) { this.transactionType = transactionType; return this; }
        public CreateTransactionRequestBuilder initialStatus(TransactionStatus initialStatus) { this.initialStatus = initialStatus; return this; }

        public CreateTransactionRequest build() {
            return new CreateTransactionRequest(customerId, amount, currency, transactionType, initialStatus);
        }
    }
}
