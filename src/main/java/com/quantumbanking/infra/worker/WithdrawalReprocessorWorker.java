package com.quantumbanking.infra.worker;

import com.quantumbanking.infra.event.AccountBalanceChangedEvent;
import com.quantumbanking.infra.exception.TransactionNotFoundException;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.service.AccountService;
import com.quantumbanking.modules.transaction.domain.Transaction;
import com.quantumbanking.modules.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WithdrawalReprocessorWorker {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void processOne(UUID transactionId) {

        Transaction transaction = transactionRepository.findByIdWithLock(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transação não encontrada: " + transactionId));

        if (!transaction.isReadyForProcessing(Instant.now())) return;

        Account account = accountService.getByIdWithLock(transaction.getOriginAccount().getId());

        try {
            transaction.startProcessing();
            account.confirmReservedDebit(transaction.getAmount());
            transaction.complete();

        } catch (Exception e) {
            log.error("Falha ao processar saque pendente, transactionId={}", transactionId, e);
            account.releaseReservation(transaction.getAmount());
            transaction.fail(e.getMessage());
        }

        accountService.save(account);
        transactionRepository.save(transaction);

        applicationEventPublisher.publishEvent(new AccountBalanceChangedEvent(account.getAccountNumber()));
    }
}
