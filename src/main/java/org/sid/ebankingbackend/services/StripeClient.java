package org.sid.ebankingbackend.services;



import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class StripeClient {


    @PostConstruct
    public void init() {
        Stripe.apiKey = "sk_test_51RUBlfR7XrO637WlNwjJXqs6ARfw3Pdfo0eaRm8Iwo7I1yS7n6szNPt4ZXJnwrHzivP30sBphK4KVsl9v9y45L2C00Vg8aKTZY";
    }

    public Customer createCustomer(String token, String email) throws StripeException {
        Map<String, Object> customerParams = new HashMap<>();
        customerParams.put("email", email);
        customerParams.put("source", token);
        return Customer.create(customerParams);
    }

    private Customer getCustomer(String id) throws StripeException {
        return Customer.retrieve(id);
    }

    public Charge chargeNewCard(String token, double amount) throws StripeException {
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", (int) (amount * 100));
        chargeParams.put("currency", "usd");
        chargeParams.put("source", token);
        return Charge.create(chargeParams);
    }

    public Charge chargeCustomerCard(String customerId, int amount) throws StripeException {
        String sourceCard = getCustomer(customerId).getDefaultSource();
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", amount);
        chargeParams.put("currency", "usd");
        chargeParams.put("customer", customerId);
        chargeParams.put("source", sourceCard);
        return Charge.create(chargeParams);
    }

    // Optional: Create and immediately charge a customer
    public Charge createCustomerAndCharge(String token, String email, double amount) throws StripeException {
        Customer customer = createCustomer(token, email);
        return chargeCustomerCard(customer.getId(), (int)(amount * 100));
    }
}
