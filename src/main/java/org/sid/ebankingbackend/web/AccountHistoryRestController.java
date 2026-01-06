package org.sid.ebankingbackend.web;

import org.sid.ebankingbackend.dtos.AccountHistoryDTO;
import org.sid.ebankingbackend.dtos.AccountOperationDTO;
import org.sid.ebankingbackend.entities.AccountOperation;
import org.sid.ebankingbackend.exceptions.BankAccountNotFoundException;
import org.sid.ebankingbackend.services.AccountHistoryServiceImpl;
import org.sid.ebankingbackend.services.CustomerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/history")
public class AccountHistoryRestController {
    @Autowired
    private  AccountHistoryServiceImpl accountHistoryService;

    public AccountHistoryRestController(AccountHistoryServiceImpl accountHistoryService) {
        this.accountHistoryService = accountHistoryService;
    }
    @GetMapping("/testC")
    public String test(){
        List<String> bankAccounts=accountHistoryService.getBankAccountIdsByEmail("client@gmail.com");
        String accountId= bankAccounts.get(0);
        System.out.println(accountId);
        return "hellllo Account controllrt";
    }
    @GetMapping("/{username}")
    public List<AccountOperation> getHistory(@PathVariable String username) {
        List<String> bankAccounts=accountHistoryService.getBankAccountIdsByEmail(username);
        if (bankAccounts.isEmpty()) {
            throw new RuntimeException("No bank account found for username: " + username);
        }        String accountId= bankAccounts.get(0);
        System.out.println(accountId);
        return accountHistoryService.accountHistory(accountId);
    }

//    @GetMapping("/{accountId}/pageOperations")
//    public AccountHistoryDTO getAccountHistory(
//            @PathVariable String accountId,
//            @RequestParam(name = "page", defaultValue = "0") int page,
//            @RequestParam(name = "size", defaultValue = "5") int size) throws BankAccountNotFoundException {
//        return accountHistoryService.getAccountHistory(accountId, page, size);
//    }
}