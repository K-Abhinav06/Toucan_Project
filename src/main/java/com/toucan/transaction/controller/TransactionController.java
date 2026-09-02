package com.toucan.transaction.controller;

import com.toucan.transaction.dto.CreateTransactionRequest;
import com.toucan.transaction.dto.TransactionResponse;
import com.toucan.transaction.dto.UpdateStatusRequest;
import com.toucan.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Provided sample REST endpoint: GET /api/sample
     */
    @GetMapping("/api/sample")
    public ResponseEntity<Map<String, String>> getSample() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "Toucan Customer Transaction Starter",
                "message", "Spring Boot Transaction Service is up and running!"
        ));
    }

    /**
     * Operation 1: Create transaction
     * POST /api/v1/transactions
     */
    @PostMapping("/api/v1/transactions")
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody CreateTransactionRequest request) {
        TransactionResponse response = transactionService.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Operation 2: Get transaction by ID
     * GET /api/v1/transactions/{id}
     */
    @GetMapping("/api/v1/transactions/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable("id") String transactionId) {
        TransactionResponse response = transactionService.getTransaction(transactionId);
        return ResponseEntity.ok(response);
    }

    /**
     * Operation 3: Update transaction status
     * PATCH /api/v1/transactions/{id}/status
     */
    @PatchMapping("/api/v1/transactions/{id}/status")
    public ResponseEntity<TransactionResponse> updateTransactionStatus(
            @PathVariable("id") String transactionId,
            @Valid @RequestBody UpdateStatusRequest request) {
        TransactionResponse response = transactionService.updateTransactionStatus(transactionId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Operation 4: Get all transactions for a customer
     * GET /api/v1/transactions/customer/{customerId}
     */
    @GetMapping("/api/v1/transactions/customer/{customerId}")
    public ResponseEntity<List<TransactionResponse>> getCustomerTransactions(
            @PathVariable("customerId") String customerId) {
        List<TransactionResponse> responses = transactionService.getCustomerTransactions(customerId);
        return ResponseEntity.ok(responses);
    }

    /**
     * Helper: List all transactions in system
     * GET /api/v1/transactions
     */
    @GetMapping("/api/v1/transactions")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }
}
