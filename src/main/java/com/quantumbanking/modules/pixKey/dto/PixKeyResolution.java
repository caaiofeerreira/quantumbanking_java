package com.quantumbanking.modules.pixKey.dto;

import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.bank.domain.bank.BankRegistry;
import com.quantumbanking.modules.pixKey.dict.DictEntry;

public record PixKeyResolution(boolean external,
                               Account internalAccount,
                               DictEntry externalEntry,
                               BankRegistry destinationBank) {
}