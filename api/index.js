const express = require('express');
const cors = require('cors');
const { v4: uuidv4 } = require('uuid');

const app = express();
app.use(cors());
app.use(express.json());

// In-memory data store for serverless execution
let transactions = [
    {
        transactionId: "a1b2c3d4-e5f6-7890-abcd-1234567890ab",
        customerId: "CUST-1001",
        amount: 450.00,
        currency: "USD",
        transactionType: "PAYMENT",
        transactionStatus: "PENDING",
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
    },
    {
        transactionId: "b2c3d4e5-f6a7-8901-bcde-2345678901bc",
        customerId: "CUST-1002",
        amount: 1200.50,
        currency: "EUR",
        transactionType: "DEPOSIT",
        transactionStatus: "COMPLETED",
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
    }
];

// Sample Endpoint
app.get('/api/sample', (req, res) => {
    res.json({
        status: "UP",
        service: "Toucan Customer Transaction Service (Vercel Serverless)",
        message: "Spring Boot & Vercel Transaction Service is up and running!"
    });
});

// Operation 1: Create Transaction
app.post('/api/v1/transactions', (req, res) => {
    const { customerId, amount, currency, transactionType, initialStatus } = req.body;

    // Business Validations
    if (!customerId || customerId.trim().length === 0) {
        return res.status(400).json({ status: 400, error: "Validation Failed", message: "Customer ID is required and must not be blank" });
    }
    if (amount === undefined || amount <= 0) {
        return res.status(400).json({ status: 400, error: "Validation Failed", message: "Transaction amount must be strictly greater than zero" });
    }
    if (!currency || !/^[A-Z]{3}$/.test(currency.toUpperCase())) {
        return res.status(400).json({ status: 400, error: "Validation Failed", message: "Currency must be a valid 3-letter ISO 4217 code (e.g. USD, EUR)" });
    }
    if (!transactionType) {
        return res.status(400).json({ status: 400, error: "Validation Failed", message: "Transaction type is required" });
    }
    if (initialStatus && initialStatus !== "PENDING") {
        return res.status(400).json({ status: 400, error: "Invalid Transaction State", message: `New transactions must be created with status PENDING. Supplied: ${initialStatus}` });
    }

    const newTx = {
        transactionId: uuidv4(),
        customerId: customerId.trim(),
        amount: parseFloat(amount),
        currency: currency.trim().toUpperCase(),
        transactionType: transactionType,
        transactionStatus: "PENDING",
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
    };

    transactions.unshift(newTx);
    res.status(201).json(newTx);
});

// Operation 2: Get Transaction by ID
app.get('/api/v1/transactions/:id', (req, res) => {
    const tx = transactions.find(t => t.transactionId === req.params.id);
    if (!tx) {
        return res.status(404).json({ status: 404, error: "Not Found", message: `Transaction not found with ID: ${req.params.id}` });
    }
    res.json(tx);
});

// Operation 3: Update Transaction Status
app.patch('/api/v1/transactions/:id/status', (req, res) => {
    const { status } = req.body;
    const tx = transactions.find(t => t.transactionId === req.params.id);
    
    if (!tx) {
        return res.status(404).json({ status: 404, error: "Not Found", message: `Transaction not found with ID: ${req.params.id}` });
    }
    if (!status) {
        return res.status(400).json({ status: 400, error: "Validation Failed", message: "New status is required" });
    }

    const currentStatus = tx.transactionStatus;
    const allowedMap = {
        'PENDING': ['PROCESSING', 'CANCELLED'],
        'PROCESSING': ['COMPLETED', 'FAILED'],
        'COMPLETED': [],
        'FAILED': [],
        'CANCELLED': []
    };

    if (currentStatus !== status && !(allowedMap[currentStatus] || []).includes(status)) {
        return res.status(400).json({
            status: 400,
            error: "Invalid Transaction State",
            message: `Invalid status transition: Cannot transition from '${currentStatus}' to '${status}'`
        });
    }

    tx.transactionStatus = status;
    tx.updatedAt = new Date().toISOString();
    res.json(tx);
});

// Operation 4: Get Customer Transactions
app.get('/api/v1/transactions/customer/:customerId', (req, res) => {
    const custId = req.params.customerId;
    const filtered = transactions.filter(t => t.customerId.toLowerCase() === custId.toLowerCase());
    res.json(filtered);
});

// Helper: List all transactions
app.get('/api/v1/transactions', (req, res) => {
    res.json(transactions);
});

module.exports = app;
