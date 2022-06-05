package com.amigoscode.testing.payment.stripe;

import com.amigoscode.testing.payment.CardPaymentCharge;
import com.amigoscode.testing.payment.CardPaymentCharger;
import com.amigoscode.testing.payment.Currency;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/*
* Class is used for integration testing
* Stripe will be disabled - everytime we run the service, this class will be injected
* */
@Service
@ConditionalOnProperty(
    value = "stripe.enabled",
    havingValue = "false"
)
public class MockStripeService implements CardPaymentCharger {
    @Override
    public CardPaymentCharge chargeCard(String source, BigDecimal amount, Currency currency,
        String description) {

        return new CardPaymentCharge(true);
    }
}
