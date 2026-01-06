package org.sid.ebankingbackend.entities;

import jakarta.persistence.SecondaryTable;
import lombok.*;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import org.sid.ebankingbackend.enums.AccountStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@DiscriminatorValue("CA")
@Data @NoArgsConstructor @AllArgsConstructor
@Getter
@Setter
public class CurrentAccount extends BankAccount {
    public CurrentAccount(String id,long balance,Customer customer, List<AccountOperation> li,double overDraft){
        super(id,balance,new Date(), AccountStatus.CREATED,customer,li);
        this.overDraft=overDraft;

    }

    private double overDraft;
}
