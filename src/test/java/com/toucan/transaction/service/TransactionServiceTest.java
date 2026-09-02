package com.toucan.transaction.service;

import com.toucan.transaction.dto.CreateTransactionRequest;
import com.toucan.transaction.dto.TransactionResponse;
import com.toucan.transaction.dto.UpdateStatusRequest;
import com.toucan.transaction.entity.Transaction;
import com.toucan.transaction.exception.InvalidTransactionStateException;
import com.toucan.transaction.exception.ResourceNotFoundException;
import com.toucan.transaction.model.TransactionStatus;
import com.toucan.transaction.model.TransactionType;
import com.toucan.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Transaction sampleTransaction;

    @BeforeEach
    void setUp() {
        sampleTransaction = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .customerId("CUST-999")
                .amount(new BigDecimal("150.50"))
                .currency("USD")
                .transactionType(TransactionType.PAYMENT)
                .transactionStatus(TransactionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Test 1: Create transaction successfully with initial status PENDING")
    void createTransaction_Success() {
        CreateTransactionRequest request = CreateTransactionRequest.builder()
                .customerId("CUST-999")
                .amount(new BigDecimal("150.50"))
                .currency("USD")
                .transactionType(TransactionType.PAYMENT)
                .initialStatus(TransactionStatus.PENDING)
                .build();

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction saved = invocation.getArgument(0);
            saved.setCreatedAt(LocalDateTime.now());
            saved.setUpdatedAt(LocalDateTime.now());
            return saved;
        });

        TransactionResponse response = transactionService.createTransaction(request);

        assertThat(response).isNotNull();
        assertThat(response.getCustomerId()).isEqualTo("CUST-999");
        assertThat(response.getAmount()).isEqualByComparingTo(new BigDecimal("150.50"));
        assertThat(response.getCurrency()).isEqualTo("USD");
        assertThat(response.getTransactionType()).isEqualTo(TransactionType.PAYMENT);
        assertThat(response.getTransactionStatus()).isEqualTo(TransactionStatus.PENDING);
        assertThat(response.getTransactionId()).isNotNull();

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Test 2: Create transaction fails when initial status is not PENDING")
    void createTransaction_FailsWithInvalidInitialStatus() {
        CreateTransactionRequest request = CreateTransactionRequest.builder()
                .customerId("CUST-999")
                .amount(new BigDecimal("200.00"))
                .currency("EUR")
                .transactionType(TransactionType.REFUND)
                .initialStatus(TransactionStatus.COMPLETED) // Invalid initial state
                .build();

        assertThatThrownBy(() -> transactionService.createTransaction(request))
                .isInstanceOf(InvalidTransactionStateException.class)
                .hasMessageContaining("New transactions must be initialized with status PENDING");

        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test 3: Create transaction fails when amount is zero or negative")
    void createTransaction_FailsWithNonPositiveAmount() {
        CreateTransactionRequest request = CreateTransactionRequest.builder()
                .customerId("CUST-999")
                .amount(new BigDecimal("-50.00"))
                .currency("USD")
                .transactionType(TransactionType.PAYMENT)
                .build();

        assertThatThrownBy(() -> transactionService.createTransaction(request))
                .isInstanceOf(InvalidTransactionStateException.class)
                .hasMessageContaining("Transaction amount must be strictly greater than zero");

        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test 4: Get transaction by ID successfully")
    void getTransaction_Success() {
        String txId = sampleTransaction.getTransactionId();
        when(transactionRepository.findById(txId)).thenReturn(Optional.of(sampleTransaction));

        TransactionResponse response = transactionService.getTransaction(txId);

        assertThat(response).isNotNull();
        assertThat(response.getTransactionId()).isEqualTo(txId);
        assertThat(response.getCustomerId()).isEqualTo("CUST-999");
    }

    @Test
    @DisplayName("Test 5: Get transaction throws ResourceNotFoundException for unknown ID")
    void getTransaction_NotFound() {
        String unknownId = "non-existent-uuid";
        when(transactionRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.getTransaction(unknownId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Transaction not found with ID: " + unknownId);
    }

    @Test
    @DisplayName("Test 6: Update status from PENDING to PROCESSING succeeds")
    void updateTransactionStatus_Success() {
        String txId = sampleTransaction.getTransactionId();
        when(transactionRepository.findById(txId)).thenReturn(Optional.of(sampleTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateStatusRequest request = new UpdateStatusRequest(TransactionStatus.PROCESSING);
        TransactionResponse response = transactionService.updateTransactionStatus(txId, request);

        assertThat(response.getTransactionStatus()).isEqualTo(TransactionStatus.PROCESSING);
        verify(transactionRepository, times(1)).save(sampleTransaction);
    }

    @Test
    @DisplayName("Test 7: Update status fails for invalid transition (PENDING -> COMPLETED directly)")
    void updateTransactionStatus_InvalidTransition() {
        String txId = sampleTransaction.getTransactionId();
        when(transactionRepository.findById(txId)).thenReturn(Optional.of(sampleTransaction));

        UpdateStatusRequest request = new UpdateStatusRequest(TransactionStatus.COMPLETED);

        assertThatThrownBy(() -> transactionService.updateTransactionStatus(txId, request))
                .isInstanceOf(InvalidTransactionStateException.class)
                .hasMessageContaining("Invalid status transition: Cannot transition transaction from 'PENDING' to 'COMPLETED'");

        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test 8: Get all transactions for customer")
    void getCustomerTransactions_Success() {
        when(transactionRepository.findByCustomerIdOrderByCreatedAtDesc("CUST-999"))
                .thenReturn(List.of(sampleTransaction));

        List<TransactionResponse> responses = transactionService.getCustomerTransactions("CUST-999");

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getCustomerId()).isEqualTo("CUST-999");
    }
}
