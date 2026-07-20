package com.quantumbanking.modules.transaction.service;

import com.quantumbanking.infra.event.AccountBalanceChangedEvent;
import com.quantumbanking.infra.exception.ResourceNotFoundException;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.service.AccountService;
import com.quantumbanking.modules.transaction.domain.Transaction;
import com.quantumbanking.modules.transaction.domain.TransactionStatus;
import com.quantumbanking.modules.transaction.repository.TransactionRepository;
import com.quantumbanking.external.ExternalLiquidationSimulator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionProcessingService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final ExternalLiquidationSimulator liquidationSimulator;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void processTransaction(UUID transactionId) {

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transação não encontrada para processamento: " + transactionId));

        if (transaction.getStatus() != TransactionStatus.PENDING) {
            log.info("Transação {} já processada anteriormente (status atual: {}). Ignorando.",
                    transactionId, transaction.getStatus());
            return;
        }

        transaction.startProcessing();
        transactionRepository.save(transaction);

        boolean liquidated = liquidationSimulator.simulate();

        Account originAccount = accountService.getByIdWithLock(transaction.getOriginAccount().getId());

        if (liquidated) {
            originAccount.confirmReservedDebit(transaction.getAmount());
            transaction.complete();
        } else {
            originAccount.releaseReservation(transaction.getAmount());
            transaction.fail("Não foi possível concluir o Pix. O valor foi estornado para sua conta.");
        }

        accountService.save(originAccount);
        transactionRepository.save(transaction);

        applicationEventPublisher.publishEvent(
                new AccountBalanceChangedEvent(Set.of(originAccount.getAccountNumber())));

        log.info("Transação {} processada. Status final: {}", transactionId, transaction.getStatus());
    }
}