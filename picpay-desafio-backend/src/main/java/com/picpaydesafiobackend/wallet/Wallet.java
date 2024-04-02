package com.picpaydesafiobackend.wallet;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("WALLET")
public record Wallet(

        @Id
        Long id,
        String fullName,
        Long cpf,
        String email,
        String password,
        WalletType type,
        BigDecimal balance

) {
}
