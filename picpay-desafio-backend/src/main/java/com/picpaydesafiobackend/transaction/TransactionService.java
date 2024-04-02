package com.picpaydesafiobackend.transaction;

import com.picpaydesafiobackend.authorization.AuthorizerService;
import com.picpaydesafiobackend.notification.NotificationService;
import com.picpaydesafiobackend.wallet.Wallet;
import com.picpaydesafiobackend.wallet.WalletRepository;
import com.picpaydesafiobackend.wallet.WalletType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionService.class);
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final AuthorizerService authorizerService;
    private final NotificationService notificationService;

    public TransactionService(TransactionRepository transactionRepository,
                              WalletRepository walletRepository, AuthorizerService authorizerService,
                              NotificationService notificationService) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.authorizerService = authorizerService;
        this.notificationService = notificationService;
    }

    public List<Transaction> list() {
        return transactionRepository.findAll();
    }

    @Transactional
    public Transaction create(Transaction transaction) {
        validate(transaction);

        var newTransaction = transactionRepository.save(transaction);

        var walletPayer = walletRepository.findById(transaction.payer()).get();
        var walletPayee = walletRepository.findById(transaction.payee()).get();
        walletRepository.save(walletPayer.debit(transaction.value()));
        walletRepository.save(walletPayee.credit(transaction.value()));

        authorizerService.authorize(transaction);
        notificationService.notify(transaction);

        return newTransaction;
    }

    private void validate(Transaction transaction) {
        LOGGER.info("validating transaction {}...", transaction);

        walletRepository.findById(transaction.payee())
                .map(payee -> walletRepository.findById(transaction.payer())
                        .map(payer -> isTransactionValid(transaction, payer) ? true : null)
                        .orElseThrow(() -> new InvalidTransactionException("Invalid Transaction - " + transaction)))
                .orElseThrow(() -> new InvalidTransactionException("Invalid Transaction - " + transaction));
    }

    private boolean isTransactionValid(Transaction transaction, Wallet payer) {
        return payer.type() == WalletType.COMUM.getValue() &&
                payer.balance().compareTo(transaction.value()) >= 0 &&
                !payer.id().equals(transaction.payee());
    }
}
