package org.sid.ebankingbackend.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sid.ebankingbackend.dtos.CustomerDTO;
import org.sid.ebankingbackend.entities.BankAccount;
import org.sid.ebankingbackend.entities.Customer;
import org.sid.ebankingbackend.exceptions.CustomerNotFoundException;
import org.sid.ebankingbackend.repositories.CustomerRepository;
import org.sid.ebankingbackend.services.AccountHistoryServiceImpl;
import org.sid.ebankingbackend.services.BankAccountServiceImpl;
import org.sid.ebankingbackend.services.CustomerServiceImpl;
import org.sid.ebankingbackend.services.Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@CrossOrigin("*")
@RequestMapping("/customers")
public class CustomerRestController {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private AccountHistoryServiceImpl accountHistoryService;
    @Autowired
    private BankAccountServiceImpl bankAccountService;
    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("/test")
    public String test() {
        return "hellllo Customer Controller";
    }

    @GetMapping
    public List<CustomerDTO> customers() {
        log.info("Fetching all customers");
        return customerService.listCustomers();
    }

    @GetMapping("/search")
    public List<CustomerDTO> searchCustomers(@RequestParam(name = "keyword", defaultValue = "") String keyword) {
        log.info("Searching customers with keyword: {}", keyword);
        return customerService.searchCustomers("%" + keyword + "%");
    }


    @GetMapping("/accounts/{username}")
    public List<BankAccount> getAccountsByUser(@PathVariable String username){
        Long id=customerService.getIdByEmail(username);
        return bankAccountService.getAccountsByCustomerId(id);
    }
//    @GetMapping("/{id}")
//    public CustomerDTO getCustomer(@PathVariable(name = "id") Long customerId) throws CustomerNotFoundException {
//        log.info("Fetching customer with ID: {}", customerId);
//        return customerService.getCustomer(customerId);
//    }

    @GetMapping("/{email}")
    public Customer getCustomerByEmail(@PathVariable(name = "email") String email) throws CustomerNotFoundException {
        log.info("Fetching customer with ID: {}", email);
        return customerRepository.findByEmail(email);
    }

    @PostMapping
    public ResponseEntity<?> saveCustomer(@RequestBody CustomerDTO customerDTO) {
        log.info("Saving new customer: {}", customerDTO.getName());
        // Manual validation
        if (customerDTO.getName() == null || customerDTO.getName().trim().isEmpty()) {
            log.error("Invalid customer data: Name is required");
            return new ResponseEntity<>("Name is required", HttpStatus.BAD_REQUEST);
        }
        if (customerDTO.getEmail() == null || !customerDTO.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            log.error("Invalid customer data: Invalid email format");
            return new ResponseEntity<>("Invalid email format", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(customerService.saveCustomer(customerDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long customerId, @RequestBody CustomerDTO customerDTO) {
        log.info("Updating customer with ID: {}", customerId);
        // Manual validation
        if (customerDTO.getName() == null || customerDTO.getName().trim().isEmpty()) {
            log.error("Invalid customer data: Name is required");
            return new ResponseEntity<>("Name is required", HttpStatus.BAD_REQUEST);
        }
        if (customerDTO.getEmail() == null || !customerDTO.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            log.error("Invalid customer data: Invalid email format");
            return new ResponseEntity<>("Invalid email format", HttpStatus.BAD_REQUEST);
        }
        customerDTO.setId(customerId);
        return new ResponseEntity<>(customerService.updateCustomer(customerDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable Long id) {
        log.info("Deleting customer with ID: {}", id);
        customerService.deleteCustomer(id);
    }
}