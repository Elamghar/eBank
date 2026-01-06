package org.sid.ebankingbackend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.sid.ebankingbackend.entities.BankAccount;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@Getter
@Setter
public class CustomerDTO {
    long id;
    private String name;
    private String email;
    private String passwd;
    private long balance;
    private    String phone;
    private  String address;
    private LocalDate DateInsc;
    private String iban;
    private Set<String> roles;
    private String cin;

}
