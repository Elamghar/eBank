package org.sid.ebankingbackend.web;

import org.sid.ebankingbackend.dtos.CreditDTO;
import org.sid.ebankingbackend.dtos.DebitDTO;
import org.sid.ebankingbackend.dtos.TransferRequestDTO;
import org.sid.ebankingbackend.entities.AccountOperation;
import org.sid.ebankingbackend.exceptions.BalanceNotSufficientException;
import org.sid.ebankingbackend.exceptions.BankAccountNotFoundException;
import org.sid.ebankingbackend.services.TransactionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("*")
@RequestMapping("/transactions")
public class TransactionRestController {
    @Autowired
    private TransactionServiceImpl transactionService;

    public TransactionRestController(TransactionServiceImpl transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public List<AccountOperation> transactions(){
        return this.transactionService.listes_transactions();
    }

    @GetMapping("/test")
    public String test() {
        return "hellllo Transaction Controller";
    }

    @GetMapping("/stats")
    public Map<String, Object> getTransactionStats() {
        List<AccountOperation> allTransactions = transactionService.listes_transactions();

        // Get today's date
        LocalDate today = LocalDate.now();
        Date todayStart = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date todayEnd = Date.from(today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Filter today's transactions
        List<AccountOperation> todayTransactions = allTransactions.stream()
                .filter(transaction -> transaction.getOperationDate().after(todayStart) &&
                        transaction.getOperationDate().before(todayEnd))
                .toList();

        // Get yesterday's date for comparison
        LocalDate yesterday = today.minusDays(1);
        Date yesterdayStart = Date.from(yesterday.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date yesterdayEnd = Date.from(yesterday.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        List<AccountOperation> yesterdayTransactions = allTransactions.stream()
                .filter(transaction -> transaction.getOperationDate().after(yesterdayStart) &&
                        transaction.getOperationDate().before(yesterdayEnd))
                .toList();

        // Calculate statistics
        int todayCount = todayTransactions.size();
        int yesterdayCount = yesterdayTransactions.size();

        // Calculate percentage change
        double percentageChange = 0.0;
        if (yesterdayCount > 0) {
            percentageChange = ((todayCount - yesterdayCount) * 100.0) / yesterdayCount;
        } else if (todayCount > 0) {
            percentageChange = 100.0; // 100% increase if yesterday was 0
        }

        // Calculate total volume for today
        double todayVolume = todayTransactions.stream()
                .mapToDouble(AccountOperation::getAmount)
                .sum();

        double yesterdayVolume = yesterdayTransactions.stream()
                .mapToDouble(AccountOperation::getAmount)
                .sum();

        double volumePercentageChange = 0.0;
        if (yesterdayVolume > 0) {
            volumePercentageChange = ((todayVolume - yesterdayVolume) * 100.0) / yesterdayVolume;
        } else if (todayVolume > 0) {
            volumePercentageChange = 100.0;
        }

        // Count by transaction type
        Map<String, Long> transactionsByType = allTransactions.stream()
                .collect(Collectors.groupingBy(
                        transaction -> transaction.getType().toString(),
                        Collectors.counting()
                ));
        System.out.println("trabsaction by type" +transactionsByType);

        // Build response
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTransactions", allTransactions.size()/2);
        stats.put("todayTransactions", todayCount);
        stats.put("yesterdayTransactions", yesterdayCount);
        stats.put("dailyPercentageChange", Math.round(percentageChange * 100.0) / 100.0);
        stats.put("todayVolume", Math.round(todayVolume * 100.0) / 100.0);
        stats.put("yesterdayVolume", Math.round(yesterdayVolume * 100.0) / 100.0);
        stats.put("volumePercentageChange", Math.round(volumePercentageChange * 100.0) / 100.0);
        stats.put("transactionsByType", transactionsByType);

        return stats;
    }

    @GetMapping("/today")
    public List<AccountOperation> getTodayTransactions() {
        List<AccountOperation> allTransactions = transactionService.listes_transactions();

        LocalDate today = LocalDate.now();
        Date todayStart = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date todayEnd = Date.from(today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        return allTransactions.stream()
                .filter(transaction -> transaction.getOperationDate().after(todayStart) &&
                        transaction.getOperationDate().before(todayEnd))
                .collect(Collectors.toList());
    }

    @GetMapping("/summary")
    public Map<String, Object> getTransactionSummary() {
        List<AccountOperation> allTransactions = transactionService.listes_transactions();
        Map<String, Object> transactionStats = getTransactionStats();

        Map<String, Object> summary = new HashMap<>();
        summary.put("transactions", allTransactions);
        summary.put("stats", transactionStats);

        return summary;
    }

    @PostMapping("/debit")
    public DebitDTO debit(@RequestBody DebitDTO debitDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
        transactionService.debit(debitDTO.getAccountId(), debitDTO.getAmount(), debitDTO.getDescription());
        return debitDTO;
    }

    @PostMapping("/credit")
    public CreditDTO credit(@RequestBody CreditDTO creditDTO) throws BankAccountNotFoundException {
        transactionService.credit(creditDTO.getAccountId(), creditDTO.getAmount(), creditDTO.getDescription());
        return creditDTO;
    }

//    @PostMapping("/transfer")
//    public void transfer(@RequestBody TransferRequestDTO transferRequestDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
//        transactionService.transfer(
//                transferRequestDTO.getAccountSource(),
//                transferRequestDTO.getAccountDestination(),
//                transferRequestDTO.getAmount());
//    }
}