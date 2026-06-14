package com.quantumbanking.modules.transaction.dto;

import com.quantumbanking.modules.account.domain.Account;

public record AccountPair(Account originAccount, Account destinationAccount) {
}