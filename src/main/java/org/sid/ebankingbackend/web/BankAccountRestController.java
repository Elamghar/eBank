package org.sid.ebankingbackend.web;

import org.sid.ebankingbackend.dtos.BankAccountDTO;
import org.sid.ebankingbackend.exceptions.BankAccountNotFoundException;
import org.sid.ebankingbackend.services.BankAccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/accounts")
public class BankAccountRestController {
    @Autowired
    private  BankAccountServiceImpl bankAccountService;

    @GetMapping("/test")
    public String test() {
        return "hellllo Banck Controller";
    }
    @GetMapping("/{accountId}")
    public BankAccountDTO getBankAccount(@PathVariable String accountId) throws BankAccountNotFoundException {
        return bankAccountService.getBankAccount(accountId);
    }

    @GetMapping
    public List<BankAccountDTO> listAccounts() {
        System.out.println("hellllllo"+bankAccountService.bankAccountList());
        return bankAccountService.bankAccountList();
    }

    @GetMapping("/count")
    public Map<String, Object> getAccountsCount() {
        List<BankAccountDTO> allAccounts = bankAccountService.bankAccountList();

        // Count accounts by type
        Map<String, Long> accountsByType = allAccounts.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        BankAccountDTO::getType,
                        java.util.stream.Collectors.counting()
                ));

        Map<String, Object> response = new HashMap<>();
        response.put("totalAccounts", allAccounts.size());
        response.put("accountsByType", accountsByType);

        return response;
    }

    @GetMapping("/summary")
    public Map<String, Object> getAccountsSummary() {
        List<BankAccountDTO> allAccounts = bankAccountService.bankAccountList();

        Map<String, Long> accountsByType = allAccounts.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        BankAccountDTO::getType,
                        java.util.stream.Collectors.counting()
                ));

        int totalAccounts = allAccounts.size();

        // Déterminer la date 1 mois avant aujourd'hui
        LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);

        // Convertir Date en LocalDate
        int totalAccountsLastMonth = (int) allAccounts.stream()
                .filter(acc -> {
                    if (acc.getCreatedAt() == null) return false;
                    return acc.getCreatedAt().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                            .isBefore(oneMonthAgo);
                })
                .count();

        long accountsCreatedThisMonth = allAccounts.stream()
                .filter(acc -> acc.getCreatedAt() != null &&
                        acc.getCreatedAt().toInstant().atZone(ZoneId.systemDefault())
                                .toLocalDate().isAfter(oneMonthAgo))
                .count();

        Map<String, Object> summary = new HashMap<>();
        summary.put("accounts", allAccounts);
        summary.put("totalAccounts", totalAccounts);
        summary.put("accountsByType", accountsByType);
        summary.put("totalAccountsLastMonth", totalAccountsLastMonth);
        summary.put("accountsCreatedThisMonth", accountsCreatedThisMonth);

        return summary;
    }


}