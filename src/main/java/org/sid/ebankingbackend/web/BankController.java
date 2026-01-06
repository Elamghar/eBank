package org.sid.ebankingbackend.web;



import com.stripe.exception.StripeException;
import org.sid.ebankingbackend.dtos.CustomerDTO;
import org.sid.ebankingbackend.dtos.DeleteDto;
import org.sid.ebankingbackend.dtos.TransferRequestDTO;
import org.sid.ebankingbackend.dtos.requestDto;
import org.sid.ebankingbackend.entities.BankAccount;
import org.sid.ebankingbackend.entities.Customer;
import org.sid.ebankingbackend.repositories.BankAccountRepository;
import org.sid.ebankingbackend.repositories.CustomerRepository;
import org.sid.ebankingbackend.services.BankService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")

public class BankController {
    private final CustomerRepository customerRepository;
    private final BankAccountRepository bankAccountRepository;
    @PostMapping("/credit")
    public ResponseEntity<String> credit(@RequestParam String email, @RequestParam double amount) {
        Customer customer = customerRepository.findByEmail(email);
        BankAccount acc = customer.getAccounts().get(0);
        acc.setBalance(acc.getBalance() + amount);
        bankAccountRepository.save(acc);
        return ResponseEntity.ok("Credited");
    }

    private final BankService bankService;

    public BankController(CustomerRepository customerRepository, BankAccountRepository bankAccountRepository, BankService bankService) {
        this.customerRepository = customerRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.bankService = bankService;
    }
    @GetMapping("/test-customer")
    public Customer getCustomer() {
        Customer customer = customerRepository.findByEmail("sna@gmail.com");
        System.out.println("Email: " + customer);
        return customer;
    }
    @PostMapping("/create-customer")
    public ResponseEntity<Customer> createCustomer(@RequestBody requestDto request) throws StripeException, StripeException {
        String name = request.getName();
        String email = request.getEmail();
        String password=request.getPasswd();
        long balance= request.getBalance();
        String phone=request.getPhone();
        String address=request.getAddress();
        String cin=request.getCin();
        LocalDate registrationDate = request.getDateInsc();
        System.out.println("dkhelte"+password+" .  ............................ "+request.getBalance());
        return ResponseEntity.ok(bankService.createCustomer(name, email, password,balance,phone,address,cin,registrationDate));
    }
    @PostMapping("/delete-customer")
    public ResponseEntity<Customer> deleteCustomer(@RequestBody DeleteDto request) throws StripeException, StripeException {
        System.out.println("dkhelte"+" .  ............................ "+request.getEmail());
        return ResponseEntity.ok(bankService.deleteCustomer(request.getEmail()));
    }
    @PostMapping("/modify-customer")
    public ResponseEntity<Customer> ModifyCustomer(@RequestBody requestDto request) throws StripeException, StripeException {
        System.out.println("dkhelte"+" .  ............................ "+request);
        return ResponseEntity.ok(bankService.ModifyCustomer(request));
    }

    @PostMapping("/transfer")
    public ResponseEntity<Map<String, String>> transfer(@RequestBody TransferRequestDTO request) throws Exception {
        System.out.println("jhhhhhhhhhhhhhhhhhhhhhhhhh:"+ request );
        bankService.transfer(request.getSenderaccountId(), request.getReceiveraccountId(), request.getAmount(), request.getDescription());
        return ResponseEntity.ok(Map.of("message", "Transfer completed"));

    }
}
