package org.sid.ebankingbackend.dtos;

import lombok.*;

import java.util.List;
@Data
@Getter
@Setter
public class AccountHistoryDTO {
    private String accountId;
    private double balance;
    private int currentPage;
    private int totalPages;
    private int pageSize;
    private List<AccountOperationDTO> accountOperationDTOS;
}
