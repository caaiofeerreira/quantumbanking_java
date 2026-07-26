package com.quantumbanking.infra.config;

import com.quantumbanking.modules.bank.domain.bank.Bank;
import com.quantumbanking.modules.bank.domain.bank.BankAccount;
import com.quantumbanking.modules.bank.repository.BankRepository;
import com.quantumbanking.modules.shared.domain.user.User;
import com.quantumbanking.modules.shared.domain.user.UserRole;
import com.quantumbanking.modules.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final BankRepository bankRepository;

    private final PasswordEncoder passwordEncoder;

    @Value("${admin.cpf}")
    private String adminCpf;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.name}")
    private String adminName;

    @Value("${admin.phone}")
    private String adminPhone;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${bank.compe}")
    private String compe;

    @Value("${bank.name}")
    private String bankName;

    @Value("${bank.ispb}")
    private String ispb;

    @Value("${bank.cnpj}")
    private String cnpj;

    @Value("${bank.accountNumber}")
    private String accountNumber;

    @Value("${bank.agencyNumber}")
    private String agencyNumber;

    @Override
    public void run(ApplicationArguments args) {

        String normalizedCpf = adminCpf.replaceAll("[^0-9]", "");

        if (!userRepository.existsByCpf(adminCpf)) {
            User admin = new User(
                    adminName,
                    normalizedCpf,
                    adminPhone,
                    adminEmail,
                    passwordEncoder.encode(adminPassword),
                    UserRole.ADMIN,
                    null
            );
            userRepository.save(admin);
        }

        if (!bankRepository.existsByCompe(compe)) {
            BankAccount bankAccount = BankAccount.builder()
                    .accountNumber(accountNumber)
                    .balance(BigDecimal.ZERO)
                    .agencyNumber(agencyNumber)
                    .build();

            Bank bank = Bank.builder()
                    .name(bankName)
                    .compe(compe)
                    .ispb(ispb)
                    .account(bankAccount)
                    .cnpj(cnpj)
                    .build();

            bankRepository.save(bank);
        }
    }
}