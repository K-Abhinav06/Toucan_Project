package com.toucan.transaction.model;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public enum TransactionStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    CANCELLED;

    // Allowed status transitions mapping
    private static final Map<TransactionStatus, Set<TransactionStatus>> ALLOWED_TRANSITIONS = Map.of(
            PENDING, EnumSet.of(PROCESSING, CANCELLED),
            PROCESSING, EnumSet.of(COMPLETED, FAILED),
            COMPLETED, EnumSet.noneOf(TransactionStatus.class),
            FAILED, EnumSet.noneOf(TransactionStatus.class),
            CANCELLED, EnumSet.noneOf(TransactionStatus.class)
    );

    public boolean canTransitionTo(TransactionStatus targetStatus) {
        return ALLOWED_TRANSITIONS.getOrDefault(this, EnumSet.noneOf(TransactionStatus.class))
                .contains(targetStatus);
    }
}
