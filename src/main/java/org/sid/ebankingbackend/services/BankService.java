package org.sid.ebankingbackend.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.sid.ebankingbackend.dtos.CustomerDTO;
import org.sid.ebankingbackend.dtos.requestDto;
import org.sid.ebankingbackend.entities.*;
import org.sid.ebankingbackend.enums.OperationType;
import org.sid.ebankingbackend.exceptions.BankAccountNotFoundException;
import org.sid.ebankingbackend.mappers.BankAccountMapperImpl;
import org.sid.ebankingbackend.repositories.AccountOperationRepository;
import org.sid.ebankingbackend.repositories.BankAccountRepository;
import org.sid.ebankingbackend.repositories.CurrentAccountRepository;
import org.sid.ebankingbackend.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class BankService {

    private final CustomerRepository customerRepository;
    private final CurrentAccountRepository currentAccountRepository;
    private final BankAccountRepository bankAccountRepository;
    private final AccountOperationRepository accountOperationRepository;
    private final StripeService stripeService;
    private BankAccountMapperImpl dtoMapper;
    public Customer ModifyCustomer(requestDto cus) throws StripeException {
        Customer customer = customerRepository.findByEmail(cus.getEmail());
        if (customer == null) {
            throw new RuntimeException("Customer not found with email: " + cus.getEmail());
        }
        if (customer.getStripeCustomerId() != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("name", cus.getName());
            params.put("email", cus.getEmail());

            com.stripe.model.Customer stripeCustomer = com.stripe.model.Customer.retrieve(customer.getStripeCustomerId());
            stripeCustomer.update(params);
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        customer.setName(cus.getName());
        customer.setEmail(cus.getEmail());
        customer.setPassword(cus.getPasswd());
        customer.setPhone(cus.getPhone());
        customer.setPassword(passwordEncoder.encode(cus.getPasswd()));
        return customerRepository.save(customer);
    }
    @Transactional
    public Customer deleteCustomer(String email) throws StripeException {
        System.out.println(email + " - deleting customer");
        Customer cusToDelete = customerRepository.findByEmail(email);
        if (cusToDelete == null) {
            throw new RuntimeException("Customer not found with email: " + email);
        }
        for (BankAccount account : cusToDelete.getAccounts()) {
            List<AccountOperation> operations = account.getOperations();
            if (operations != null) {
                accountOperationRepository.deleteAll(operations);
            }
        }
        bankAccountRepository.deleteAll(cusToDelete.getAccounts());
        if (cusToDelete.getStripeCustomerId() != null) {
            com.stripe.model.Customer stripeCustomer = com.stripe.model.Customer.retrieve(cusToDelete.getStripeCustomerId());
            stripeCustomer.delete();
            System.out.println("Stripe customer deleted: " + cusToDelete.getStripeCustomerId());
        }
        customerRepository.delete(cusToDelete);
        return cusToDelete;
    }

    public BankService(CustomerRepository cr,BankAccountMapperImpl u, CurrentAccountRepository br, AccountOperationRepository ar, StripeService ss,BankAccountRepository bankAccountRepository,BankAccountMapperImpl p) {
        this.customerRepository = cr;
        this.currentAccountRepository = br;
        this.accountOperationRepository = ar;
        this.stripeService = ss;
        this.bankAccountRepository=bankAccountRepository;
        this.dtoMapper=p;
        Stripe.apiKey = "sk_test_51RUBlfR7XrO637WlNwjJXqs6ARfw3Pdfo0eaRm8Iwo7I1yS7n6szNPt4ZXJnwrHzivP30sBphK4KVsl9v9y45L2C00Vg8aKTZY";
    }

    public Customer createCustomer(String name, String email, String password, long balance, String phone, String adress,String cin, LocalDate date) throws StripeException {
        com.stripe.model.Customer stripeCustomer = stripeService.createStripeCustomer(email, name);
        System.out.println(password);
        System.out.println(email+"------------------------");
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Customer customer = new Customer(name, email, stripeCustomer.getId(), new ArrayList<>(),passwordEncoder.encode(password),Set.of("CLIENT"),phone,adress,cin,date);
        customer.setEmail(email);
        System.out.println(passwordEncoder.encode(password));
        customer.setPassword(passwordEncoder.encode(password));
        customer = customerRepository.save(customer);

        CurrentAccount account = new CurrentAccount(UUID.randomUUID().toString(), balance, customer, new ArrayList<>(),10);
        System.out.println("tesssssssssssssssssssssssssssssssssssst: "+account );
        currentAccountRepository.save(account);

        return customer;
    }

    @Transactional
    public void transfer(String  senderId, String  receiverId, double amount, String description) throws Exception {
        BankAccount senderAccount = bankAccountRepository.findById(senderId)
                .orElseThrow(() -> new BankAccountNotFoundException("BankAccount not found"));
        if (senderAccount instanceof SavingAccount) {
            dtoMapper.fromSavingBankAccount((SavingAccount) senderAccount);
        } else {
            dtoMapper.fromCurrentBankAccount((CurrentAccount) senderAccount);
        }
        BankAccount receiverAccount = bankAccountRepository.findById(receiverId)
                .orElseThrow(() -> new BankAccountNotFoundException("BankAccount not found"));
        if (receiverAccount instanceof SavingAccount) {
            dtoMapper.fromSavingBankAccount((SavingAccount) receiverAccount);
        } else {
            dtoMapper.fromCurrentBankAccount((CurrentAccount) receiverAccount);
        }
        System.out.println(senderAccount+" ...............................");
        Customer sender = senderAccount.getCustomer();
        Customer receiver = receiverAccount.getCustomer();

        if (sender == null || receiver == null) {
            throw new Exception("Sender or receiver not found");
        }
        if (sender.getAccounts().isEmpty()) {
            throw new Exception("Sender has no bank account");
        }

        if (receiver.getAccounts().isEmpty()) {
            throw new Exception("Receiver has no bank account");
        }

        BankAccount senderAcc = sender.getAccounts().get(0);
        BankAccount receiverAcc = receiver.getAccounts().get(0);
        if (senderAcc.getBalance() < amount) {
            throw new Exception("Insufficient balance");
        }
        stripeService.chargeCustomer(sender.getStripeCustomerId(), (int) (amount * 100), "usd", description);
        senderAcc.setBalance(senderAcc.getBalance() - amount);
        AccountOperation debitOp = new AccountOperation(
                null,
                new Date(),
                sender.getEmail(),
                amount,
                description,
                OperationType.DEBIT,
                senderAcc
        );
        accountOperationRepository.save(debitOp);
        bankAccountRepository.save(senderAcc);
        receiverAcc.setBalance(receiverAcc.getBalance() + amount);
        AccountOperation creditOp = new AccountOperation(
                null,
                new Date(),
                receiver.getEmail(),
                amount,
                description,
                OperationType.CREDIT,
                receiverAcc
        );
        accountOperationRepository.save(creditOp);
        bankAccountRepository.save(receiverAcc);
    }
}

