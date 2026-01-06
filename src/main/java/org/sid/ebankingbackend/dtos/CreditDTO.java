package org.sid.ebankingbackend.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CreditDTO {
    private String accountId;
    private double amount;
    private String description;
}
