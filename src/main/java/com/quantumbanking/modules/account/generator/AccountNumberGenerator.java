package com.quantumbanking.modules.account.generator;

import com.quantumbanking.modules.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class AccountNumberGenerator {

    private final AccountRepository accountRepository;

    public String generate() {

        String number;
        String fullNumber;
        do {
            number = String.format("%08d", new Random().nextInt(100000000));
            fullNumber = number + generateVerifierDigit(number);

        } while (accountRepository.existsByAccountNumber(fullNumber));

        return fullNumber;
    }

    private String generateVerifierDigit(String accountNumber) {
        int sum = 0;
        int multiplier = 2;
        for (int i = accountNumber.length() - 1; i >= 0; i--) {
            sum += Character.getNumericValue(accountNumber.charAt(i)) * multiplier;
            multiplier = multiplier == 9 ? 2 : multiplier + 1;
        }
        int remainder = sum % 11;
        if (remainder == 0 || remainder == 1) return "0";
        return String.valueOf(11 - remainder);
    }
}
