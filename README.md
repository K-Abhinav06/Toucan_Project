# 🦤 Toucan Customer Transaction Starter Project & Enterprise Console

> A modern, production-grade **Spring Boot 3.2 / Java 17** Customer Transaction Management Platform with H2 embedded database, business validation engine, comprehensive JUnit 5 test suite, Vercel serverless integration, and a glassmorphism Enterprise Web Dashboard.

[![Java 17](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot 3.2](https://img.shields.io/badge/Spring%20Boot-3.2.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Build Status](https://img.shields.io/badge/Build-Passing-success.svg)](https://github.com/K-Abhinav06/Toucan_Project)
[![Vercel Ready](https://img.shields.io/badge/Vercel-Configured-black.svg)](https://vercel.com/)

---

## 📖 Overview

The **Toucan Customer Transaction Starter Project** provides a resilient RESTful API architecture for financial and customer transaction management. It includes strict domain validation rules, automated status state machine transitions, embedded H2 database persistence, and an interactive Developer Dashboard.

---

## ✨ Features

- **4 Core REST Operations**:
  1. `POST /api/v1/transactions` — Create transaction
  2. `GET /api/v1/transactions/{id}` — Get transaction by ID
  3. `PATCH /api/v1/transactions/{id}/status` — Update transaction status
  4. `GET /api/v1/transactions/customer/{customerId}` — Get all transactions for a customer
  - Plus sample health endpoint `GET /api/sample`
- **Strict Business Validation Engine**:
  - **Transaction ID**: Auto-generated 36-character UUID.
  - **Customer ID**: Mandatory non-blank string (`3 to 64 characters`).
  - **Amount**: Must be strictly positive (`> 0.00`).
  - **Currency**: Validated 3-letter ISO 4217 currency code (`USD`, `EUR`, `GBP`, `INR`, `CAD`, `JPY`).
  - **Transaction Type**: Validated enum (`PAYMENT`, `REFUND`, `TRANSFER`, `DEPOSIT`, `WITHDRAWAL`).
  - **Initial Status**: New transactions are strictly initialized with status `PENDING`.
  - **Status State Machine**:
    - `PENDING` ➔ `PROCESSING` or `CANCELLED`
    - `PROCESSING` ➔ `COMPLETED` or `FAILED`
    - Terminal states (`COMPLETED`, `FAILED`, `CANCELLED`) cannot transition further.
- **Toucan Enterprise Web Console**: Single-page Web UI with financial volume stats, transaction table, status workflow modals, customer lookup, and API docs.
- **Dual Runtime Support**: Runs as a standard Spring Boot JVM service OR as Vercel serverless functions.

---

## 📂 Project Structure

```text
Toucan_Project/
├── api/
│   └── index.js                   # Vercel Serverless Express API Handler
├── src/
│   ├── main/
│   │   ├── java/com/toucan/transaction/
│   │   │   ├── controller/        # REST Endpoints
│   │   │   ├── dto/               # Request & Response DTOs
│   │   │   ├── entity/            # JPA Entities (Transaction)
│   │   │   ├── exception/         # Global Exception Handler
│   │   │   ├── model/             # Enums (TransactionType, TransactionStatus)
│   │   │   ├── repository/        # Spring Data JPA Repository
│   │   │   └── service/           # Business Logic & Validation Engine
│   │   └── resources/
│   │       ├── application.yml    # H2 Database & Server Settings
│   │       └── static/index.html  # Toucan Web Console UI
│   └── test/java/com/toucan/transaction/
│       ├── controller/            # Controller MockMvc Integration Tests
│       ├── service/               # Service Unit Tests
│       └── TransactionApplicationTests.java
├── Dockerfile                      # Cloud Container Build Setup (Render/Railway)
├── mvnw.cmd                        # Windows Maven Wrapper
├── package.json                    # npm Scripts & Vercel Config
├── pom.xml                         # Maven Build File (Spring Boot 3.2, Java 17)
├── README.md                       # Comprehensive Documentation
└── vercel.json                     # Vercel Deployment Configuration
```

---

## 🚀 Quick Start (Local Run)

### Prerequisites
- **Java 17** (or Java 21 / 25)
- **Node.js** (Optional, for `npm` wrapper commands)

### Running the Application

Using **npm**:
```bash
npm run dev
```

Or using **Maven Wrapper**:
```bash
mvnw.cmd spring-boot:run
```

Once started:
- 🌐 **Toucan Web Dashboard**: [http://localhost:8080/](http://localhost:8080/)
- 🗄️ **H2 Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
  - **JDBC URL**: `jdbc:h2:mem:toucandb`
  - **Username**: `sa`
  - **Password**: *(leave blank)*

---

## 🧪 Running Tests

Execute the 16 unit and integration test suite:

```bash
mvnw.cmd clean test
```

### Test Suite Output
```text
[INFO] Running com.toucan.transaction.controller.TransactionControllerTest
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.toucan.transaction.service.TransactionServiceTest
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.toucan.transaction.TransactionApplicationTests
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

---

## 📡 API Specification

### 1. Create Transaction
- **Endpoint**: `POST /api/v1/transactions`
- **Request Body**:
```json
{
  "customerId": "CUST-1001",
  "amount": 450.00,
  "currency": "USD",
  "transactionType": "PAYMENT"
}
```
- **Response (201 Created)**:
```json
{
  "transactionId": "a1b2c3d4-e5f6-7890-abcd-1234567890ab",
  "customerId": "CUST-1001",
  "amount": 450.0,
  "currency": "USD",
  "transactionType": "PAYMENT",
  "transactionStatus": "PENDING",
  "createdAt": "2026-09-02T12:00:00",
  "updatedAt": "2026-09-02T12:00:00"
}
```

### 2. Get Transaction by ID
- **Endpoint**: `GET /api/v1/transactions/{id}`
- **Response (200 OK)**:
```json
{
  "transactionId": "a1b2c3d4-e5f6-7890-abcd-1234567890ab",
  "customerId": "CUST-1001",
  "amount": 450.0,
  "currency": "USD",
  "transactionType": "PAYMENT",
  "transactionStatus": "PENDING"
}
```

### 3. Update Transaction Status
- **Endpoint**: `PATCH /api/v1/transactions/{id}/status`
- **Request Body**:
```json
{
  "status": "PROCESSING"
}
```
- **Response (200 OK)**:
```json
{
  "transactionId": "a1b2c3d4-e5f6-7890-abcd-1234567890ab",
  "transactionStatus": "PROCESSING"
}
```

### 4. Get Customer Transactions
- **Endpoint**: `GET /api/v1/transactions/customer/{customerId}`
- **Response (200 OK)**:
```json
[
  {
    "transactionId": "a1b2c3d4-e5f6-7890-abcd-1234567890ab",
    "customerId": "CUST-1001",
    "amount": 450.0,
    "currency": "USD",
    "transactionType": "PAYMENT",
    "transactionStatus": "PROCESSING"
  }
]
```

---

## 🌐 Cloud Deployment

### ⚡ Option A: Vercel Deployment
Deploy both Web UI and Serverless API in one command:
```bash
npx vercel --prod
```
