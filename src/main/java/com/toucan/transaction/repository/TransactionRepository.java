package com.toucan.transaction.repository;

import com.toucan.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    /**
     * Find all transactions belonging to a specific customer ID sorted by creation time descending.
     *
     * @param customerId Customer ID string
     * @return List of matching Transaction entities
     */
    List<Transaction> findByCustomerIdOrderByCreatedAtDesc(String customerId);
}
