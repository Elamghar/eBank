package org.sid.ebankingbackend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.sid.ebankingbackend.enums.AccountStatus;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE",length = 4)
@Data @NoArgsConstructor @AllArgsConstructor
@Getter
@Setter
public abstract class BankAccount {
    public Customer getCustomer() {
        return customer;
    }

    @Id
    private String id;
    private double balance;
    private Date createdAt;
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
    @ManyToOne
    @JsonIgnore
    private Customer customer;

    @OneToMany(mappedBy = "bankAccount")
    @JsonIgnore
    private List<AccountOperation> operations;
}
