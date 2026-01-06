package org.sid.ebankingbackend.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sid.ebankingbackend.entities.AccountOperation;
import org.sid.ebankingbackend.entities.BankAccount;
import org.sid.ebankingbackend.enums.OperationType;
import org.sid.ebankingbackend.exceptions.BalanceNotSufficientException;
import org.sid.ebankingbackend.exceptions.BankAccountNotFoundException;
import org.sid.ebankingbackend.mappers.BankAccountMapperImpl;
import org.sid.ebankingbackend.repositories.AccountOperationRepository;
import org.sid.ebankingbackend.repositories.BankAccountRepository;
import org.sid.ebankingbackend.services.Service.AuditService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sid.ebankingbackend.services.Service.TransactionService;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    private BankAccountRepository bankAccountRepository;
    private AccountOperationRepository accountOperationRepository;
    private BankAccountMapperImpl dtoMapper;
    private AuditServiceImp auditService;

    public List<AccountOperation> listes_transactions() {
        return accountOperationRepository.findAll();
    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Debit amount must be positive");
        }
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank account not found: " + accountId));
        if (bankAccount.getBalance() < amount) {
            throw new BalanceNotSufficientException("Insufficient balance for account: " + accountId);
        }

        log.info("Debiting {} from account {}", amount, accountId);
        auditService.log("DEBIT", "SYSTEM", "Debiting " + amount + " from account " + accountId);

        saveAccountOperation(bankAccount, amount, description, OperationType.DEBIT);
        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountRepository.save(bankAccount);
    }


    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Credit amount must be positive");
        }
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank account not found: " + accountId));

        log.info("Crediting {} to account {}", amount, accountId);
        auditService.log("CREDIT", "SYSTEM", "Crediting " + amount + " to account " + accountId); // ✅ Audit log

        saveAccountOperation(bankAccount, amount, description, OperationType.CREDIT);
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        bankAccountRepository.save(bankAccount);
    }


    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException {
       //Aprés andir blast system: username =>tanchof how to make it with joins based on Ids
        auditService.log("TRANSFER", "SYSTEM", "Transfer of " + amount + " from " + accountIdSource + " to " + accountIdDestination); // ✅ Audit log
        debit(accountIdSource, amount, "Transfer to " + accountIdDestination);
        credit(accountIdDestination, amount, "Transfer from " + accountIdSource);
    }


    private void saveAccountOperation(BankAccount bankAccount, double amount, String description, OperationType type) {
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(type);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
    }
}
