package com.picpaydesafiobackend.transaction;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("TRANSACTIONS")
public record Transaction(

        @Id
        String id,
        Long payer,
        Long payee,
        BigDecimal value,
        @CreatedDate
        LocalDateTime createdAt) {

    public Transaction {
        value = value.setScale(2);
    }
}