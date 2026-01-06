package org.sid.ebankingbackend.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@DiscriminatorValue("CLIENT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
public class Customer extends User {
    private String name;
    private String Cin;
    private String address;
    private String phone;
    private LocalDate DateInscription;

    public Customer(String name, String email, String stripeCustomerId, List<BankAccount> li,String password,Set<String> roles,String phone,String adress,String cin,LocalDate date){
        super(null,email,password, Set.of("CLIENT"));
        this.name=name;
        this.stripeCustomerId=stripeCustomerId;
        this.accounts=li;
        this.Cin=cin;
        this.address=adress;
        this.DateInscription=date;
        this.phone=phone;
    }
    @OneToMany(mappedBy = "customer",fetch=FetchType.EAGER)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<BankAccount> accounts;
    @Column(name = "stripe_customer_id")
    private String stripeCustomerId;

    public List<BankAccount> getBankAccounts() {
        return accounts;
    }
}