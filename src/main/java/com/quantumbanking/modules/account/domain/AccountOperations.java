package com.quantumbanking.modules.account.domain;

import java.math.BigDecimal;

public interface AccountOperations {

    void credit(BigDecimal amount);

    void debit(BigDecimal amount);
}