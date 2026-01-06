package org.sid.ebankingbackend.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class TransferRequestDTO {
    private String senderaccountId;
    private String receiveraccountId;
    private double amount;
    private String description;

}
