package org.sid.ebankingbackend.entities;

import lombok.*;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("SA")
@Data @NoArgsConstructor @AllArgsConstructor
@Getter
@Setter
public class SavingAccount extends BankAccount {
    private double interestRate;
}
