package com.quantumbanking.modules.client.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.client.dto.ClientRegistrationDTO;
import com.quantumbanking.modules.shared.domain.user.User;
import com.quantumbanking.modules.shared.domain.user.UserRole;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "Client")
@Table(name = "tb_client")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Client extends User {

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ClientType type;

    @JsonManagedReference
    @OneToOne(mappedBy = "client", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Account account;

    public Client(ClientRegistrationDTO dto, String encryptedPassword) {
        super(
                dto.name(),
                dto.cpf(),
                dto.phone(),
                dto.email(),
                encryptedPassword,
                UserRole.CLIENT,
                dto.address()
        );
        this.type = dto.clientType();
    }

}