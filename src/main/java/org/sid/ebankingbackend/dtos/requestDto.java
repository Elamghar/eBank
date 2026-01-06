package org.sid.ebankingbackend.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

import java.util.Set;

@Data
@Getter
@Setter
public class requestDto {
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
