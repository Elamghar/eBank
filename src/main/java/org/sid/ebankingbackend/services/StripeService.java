package org.sid.ebankingbackend.services;


import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StripeService {
    @PostConstruct
    public void init() {
        Stripe.apiKey = "sk_test_51RUBlfR7XrO637WlNwjJXqs6ARfw3Pdfo0eaRm8Iwo7I1yS7n6szNPt4ZXJnwrHzivP30sBphK4KVsl9v9y45L2C00Vg8aKTZY";
    }
    public Customer createStripeCustomer(String email, String name) throws StripeException {
        // 1. Create a connected account
        Map<String, Object> accountParams = new HashMap<>();
        accountParams.put("type", "custom");
        accountParams.put("country", "US");
        accountParams.put("email", email);
        accountParams.put("business_type", "individual");

        // Enable transfers capability
        Map<String, Object> capabilities = new HashMap<>();
        Map<String, Object> transfersCapability = new HashMap<>();
        transfersCapability.put("requested", true);
        capabilities.put("transfers", transfersCapability);
        accountParams.put("capabilities", capabilities);

        // Accept Terms of Service
        Map<String, Object> tosAcceptance = new HashMap<>();
        tosAcceptance.put("date", System.currentTimeMillis() / 1000L);
        tosAcceptance.put("ip", "127.0.0.1"); // Replace with the actual client IP in production
        accountParams.put("tos_acceptance", tosAcceptance);

        // Create the connected account
//        Account account = Account.create(accountParams);
//        Map<String, Object> bankAccountParams = new HashMap<>();
//        bankAccountParams.put("object", "bank_account");
//        bankAccountParams.put("country", "FR");
//        bankAccountParams.put("currency", "eur");
//        bankAccountParams.put("account_holder_name", name);
//        bankAccountParams.put("account_holder_type", "individual");
//        bankAccountParams.put("account_number", iban); // IBAN from RIB

//        Map<String, Object> externalAccountParams = new HashMap<>();
//        externalAccountParams.put("external_account", bankAccountParams);
//
//        account.getExternalAccounts().create(externalAccountParams);
        Map<String, Object> customerParams = new HashMap<>();
        customerParams.put("email", email);
        customerParams.put("name", name);
        customerParams.put("source", "tok_visa");
        Customer customer = Customer.create(customerParams);


        return customer;
    }




    public Charge chargeCustomer(String customerId, double amount, String currency, String description) throws StripeException {
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", (int)(amount * 100));
        chargeParams.put("currency", currency);
        chargeParams.put("customer", customerId);
        chargeParams.put("description", description);
        return Charge.create(chargeParams);
    }
}
