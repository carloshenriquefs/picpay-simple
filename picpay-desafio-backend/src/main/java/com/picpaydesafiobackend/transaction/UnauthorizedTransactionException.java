package com.picpaydesafiobackend.transaction;

public class UnauthorizedTransactionException extends RuntimeException {

    public UnauthorizedTransactionException(String message) {
        super(message);
    }
}
