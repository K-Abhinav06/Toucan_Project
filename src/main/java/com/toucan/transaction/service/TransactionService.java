package com.toucan.transaction.service;

import com.toucan.transaction.dto.CreateTransactionRequest;
import com.toucan.transaction.dto.TransactionResponse;
import com.toucan.transaction.dto.UpdateStatusRequest;
import com.toucan.transaction.entity.Transaction;
import com.toucan.transaction.exception.InvalidTransactionStateException;
import com.toucan.transaction.exception.ResourceNotFoundException;
import com.toucan.transaction.model.TransactionStatus;
import com.toucan.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    /**
     * Operation 1: Create Transaction
     * Business validation:
     * - Initial status must be PENDING (if supplied, non-PENDING throws InvalidTransactionStateException).
     * - Amount must be strictly greater than 0.
     * - Generates a unique UUID transaction ID.
     */
    @Transactional
    public TransactionResponse createTransaction(CreateTransactionRequest request) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionStateException("Transaction amount must be strictly greater than zero");
        }

        TransactionStatus initialStatus = request.getInitialStatus();
        if (initialStatus != null && initialStatus != TransactionStatus.PENDING) {
            throw new InvalidTransactionStateException(
                    "New transactions must be initialized with status PENDING. Supplied status: " + initialStatus
            );
        }

        Transaction transaction = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .customerId(request.getCustomerId().trim())
                .amount(request.getAmount())
                .currency(request.getCurrency().trim().toUpperCase())
                .transactionType(request.getTransactionType())
                .transactionStatus(TransactionStatus.PENDING)
                .build();

        Transaction saved = transactionRepository.save(transaction);
        return TransactionResponse.fromEntity(saved);
    }

    /**
     * Operation 2: Get Transaction by ID
     */
    @Transactional(readOnly = true)
    public TransactionResponse getTransaction(String transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + transactionId));
        return TransactionResponse.fromEntity(transaction);
    }

    /**
     * Operation 3: Update Transaction Status
     * Business validation:
     * - Verifies valid state transition according to TransactionStatus state machine.
     * - Terminal states (COMPLETED, FAILED, CANCELLED) cannot be changed.
     */
    @Transactional
    public TransactionResponse updateTransactionStatus(String transactionId, UpdateStatusRequest request) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + transactionId));

        TransactionStatus currentStatus = transaction.getTransactionStatus();
        TransactionStatus targetStatus = request.getStatus();

        if (currentStatus == targetStatus) {
            // No-op if status is identical, return current
            return TransactionResponse.fromEntity(transaction);
        }

        if (!currentStatus.canTransitionTo(targetStatus)) {
            throw new InvalidTransactionStateException(
                    String.format("Invalid status transition: Cannot transition transaction from '%s' to '%s'",
                            currentStatus, targetStatus)
            );
        }

        transaction.setTransactionStatus(targetStatus);
        Transaction updated = transactionRepository.save(transaction);
        return TransactionResponse.fromEntity(updated);
    }

    /**
     * Operation 4: Get All Transactions for a Customer
     */
    @Transactional(readOnly = true)
    public List<TransactionResponse> getCustomerTransactions(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new InvalidTransactionStateException("Customer ID must not be blank");
        }
        return transactionRepository.findByCustomerIdOrderByCreatedAtDesc(customerId.trim())
                .stream()
                .map(TransactionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Helper: Get All Transactions in system
     */
    @Transactional(readOnly = true)
    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAll()
                .stream()
                .map(TransactionResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
