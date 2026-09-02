package com.toucan.transaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toucan.transaction.dto.CreateTransactionRequest;
import com.toucan.transaction.dto.TransactionResponse;
import com.toucan.transaction.dto.UpdateStatusRequest;
import com.toucan.transaction.exception.ResourceNotFoundException;
import com.toucan.transaction.model.TransactionStatus;
import com.toucan.transaction.model.TransactionType;
import com.toucan.transaction.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    private TransactionResponse sampleResponse;
    private String txId;

    @BeforeEach
    void setUp() {
        txId = UUID.randomUUID().toString();
        sampleResponse = TransactionResponse.builder()
                .transactionId(txId)
                .customerId("CUST-101")
                .amount(new BigDecimal("99.99"))
                .currency("USD")
                .transactionType(TransactionType.PAYMENT)
                .transactionStatus(TransactionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Sample Endpoint: GET /api/sample returns 200 OK")
    void getSample_Returns200OK() throws Exception {
        mockMvc.perform(get("/api/sample"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("Toucan Customer Transaction Starter"));
    }

    @Test
    @DisplayName("Operation 1: POST /api/v1/transactions creates transaction and returns 201 CREATED")
    void createTransaction_Returns201Created() throws Exception {
        CreateTransactionRequest request = CreateTransactionRequest.builder()
                .customerId("CUST-101")
                .amount(new BigDecimal("99.99"))
                .currency("USD")
                .transactionType(TransactionType.PAYMENT)
                .build();

        when(transactionService.createTransaction(any(CreateTransactionRequest.class)))
                .thenReturn(sampleResponse);

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").value(txId))
                .andExpect(jsonPath("$.customerId").value("CUST-101"))
                .andExpect(jsonPath("$.amount").value(99.99))
                .andExpect(jsonPath("$.transactionStatus").value("PENDING"));
    }

    @Test
    @DisplayName("Operation 1: POST /api/v1/transactions returns 400 BAD REQUEST when invalid body")
    void createTransaction_Returns400BadRequest_WhenValidationFails() throws Exception {
        CreateTransactionRequest invalidRequest = CreateTransactionRequest.builder()
                .customerId("") // Empty customer ID
                .amount(new BigDecimal("-10.00")) // Negative amount
                .currency("INVALID_CURRENCY") // Not 3 ISO chars
                .build();

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"));
    }

    @Test
    @DisplayName("Operation 2: GET /api/v1/transactions/{id} returns 200 OK")
    void getTransaction_Returns200OK() throws Exception {
        when(transactionService.getTransaction(txId)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/v1/transactions/{id}", txId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(txId))
                .andExpect(jsonPath("$.customerId").value("CUST-101"));
    }

    @Test
    @DisplayName("Operation 2: GET /api/v1/transactions/{id} returns 404 NOT FOUND when missing")
    void getTransaction_Returns404NotFound() throws Exception {
        when(transactionService.getTransaction("missing-id"))
                .thenThrow(new ResourceNotFoundException("Transaction not found with ID: missing-id"));

        mockMvc.perform(get("/api/v1/transactions/{id}", "missing-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Transaction not found with ID: missing-id"));
    }

    @Test
    @DisplayName("Operation 3: PATCH /api/v1/transactions/{id}/status updates status and returns 200 OK")
    void updateStatus_Returns200OK() throws Exception {
        sampleResponse.setTransactionStatus(TransactionStatus.PROCESSING);
        UpdateStatusRequest request = new UpdateStatusRequest(TransactionStatus.PROCESSING);

        when(transactionService.updateTransactionStatus(eq(txId), any(UpdateStatusRequest.class)))
                .thenReturn(sampleResponse);

        mockMvc.perform(patch("/api/v1/transactions/{id}/status", txId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionStatus").value("PROCESSING"));
    }

    @Test
    @DisplayName("Operation 4: GET /api/v1/transactions/customer/{customerId} returns list")
    void getCustomerTransactions_Returns200OK() throws Exception {
        when(transactionService.getCustomerTransactions("CUST-101"))
                .thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/v1/transactions/customer/{customerId}", "CUST-101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value("CUST-101"))
                .andExpect(jsonPath("$[0].transactionId").value(txId));
    }
}
