# Stripe_Integration_Testing

Focuses on testing and mocking a payment unit feature that uses the Stripe API.
Contains: 
- Unit testing
- Integration testing
- Mocking with Mockito

We will use a Test Driven Development format of 
```
Given

When

Then

Assertion
```

View Testing files in ../src/test/java

## Payment Testing 

Unit tests
- Making a payment request
- Charging a card
- Error testing

Integration Tests 
- Making a full payment via rest endpoint
- Verifying assertions from response payaloads
- Verifying the Stripe Service API is available

## Customer Testing 
Unit tests 
- Customer Creation
- Customer saving
- Customer registration 
