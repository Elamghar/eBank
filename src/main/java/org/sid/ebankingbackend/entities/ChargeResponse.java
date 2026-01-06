package org.sid.ebankingbackend.entities;

import com.stripe.model.Charge;

public  class ChargeResponse {
    public String id;
    public String status;
    public String currency;
    public Long amount;

    public ChargeResponse(Charge charge) {
        this.id = charge.getId();
        this.status = charge.getStatus();
        this.currency = charge.getCurrency();
        this.amount = charge.getAmount();
    }
}