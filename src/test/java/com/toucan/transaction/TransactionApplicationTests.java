package com.toucan.transaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TransactionApplicationTests {

    @Test
    @DisplayName("Context loads successfully")
    void contextLoads() {
        // Verifies Spring context initializes with H2 JPA repositories
    }
}
