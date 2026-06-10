package com.quantumbanking.modules.client.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.shared.domain.address.Address;
import com.quantumbanking.modules.shared.domain.user.User;
import com.quantumbanking.modules.shared.domain.user.UserRole;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "Client")
@Table(name = "tb_client")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Client extends User {

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ClientType type;

    @JsonManagedReference
    @OneToOne(mappedBy = "client", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Account account;

    public Client(String name, String cpf, String phone, String email,
                  String password, Address address, ClientType clientType) {
        super(name, cpf, phone, email, password, UserRole.CLIENT, address);
        this.type = clientType;
    }
}