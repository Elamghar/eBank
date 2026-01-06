package org.sid.ebankingbackend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.sid.ebankingbackend.enums.OperationType;

import jakarta.persistence.*;
import lombok.Data;
import org.sid.ebankingbackend.enums.OperationType;

import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@NamedQueries({
        @NamedQuery(
                name = "AccountOperation.findByBankAccountId",
                query = "SELECT a FROM AccountOperation a WHERE a.bankAccount.id = :accountId"
        ),
        @NamedQuery(
                name = "AccountOperation.findByBankAccountIdOrderByOperationDateDesc",
                query = "SELECT a FROM AccountOperation a WHERE a.bankAccount.id = :accountId ORDER BY a.operationDate DESC"
        )
})
public class AccountOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date operationDate;
    private String email;
    private double amount;
    private String description;
    @Enumerated(EnumType.STRING)
    private OperationType type;
    @JsonIgnore
    @ManyToOne
    private BankAccount bankAccount;
}

