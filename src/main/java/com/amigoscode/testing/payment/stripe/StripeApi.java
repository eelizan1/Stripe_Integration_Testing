package com.amigoscode.testing.payment.stripe;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import org.springframework.stereotype.Service;

import java.util.Map;

/*
* Purpose is to encapsulate the static call, Charge.create(requestMap, options), into a class
* which we can now mock
*
* */
@Service
public class StripeApi {
    public Charge create(Map<String, Object> requestMap, RequestOptions options)
        throws StripeException { // let the client deal with the exception if we throw instead of surround with try catch
        return Charge.create(requestMap, options);
    }
}
