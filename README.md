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

## Customer Testing 
Unit tests 
- Customer Creation
- Customer saving
- Customer registration

### Given, When, Then 
```
@DataJpaTest // allows us to test database queries
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void itShouldSelectCustomerByPhoneNumber() {
        // Given
        // When
        // Then
    }
}
```

### Using Assertions
```
@Test
void itShouldSaveCustomer() {
        // Given a customer
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "Abel", "404-291-3891");
        // When we save a customer
        customerRepository.save(customer);
        // Then we can retrieve the customer back
        Optional<Customer> actualCustomer = customerRepository.findById(id);

        // check if customer is available and if values match
        assertThat(actualCustomer)
            .isPresent()
            .hasValueSatisfying(c -> {
                assertThat(c.getId()).isEqualTo(id);
                assertThat(c.getName()).isEqualTo("Abel");
                assertThat(c.getPhoneNumber()).isEqualTo("404-291-3891");
            });
    }
```

## Payment Testing 

Unit tests
- Making a payment request
- Charging a card
- Error testing

Integration Tests 
- Making a full payment via rest endpoint
- Verifying assertions from response payaloads
- Verifying the Stripe Service API is available

### Integration Test Notes
We create a mock service that mock’s the [StripeService.java](http://StripeService.java) so that we dont have to make a network call when doing a full integration test 

Annotations that allow to switch between the mock and actual service
```
MockStripeService 

@Service
@ConditionalOnProperty(
    value = "stripe.enabled",
    havingValue = "false"
)

StripeService 

@Service
@ConditionalOnProperty(
    value = "stripe.enabled",
    havingValue = "true"
)

application.properties 

stripe.enabled=false
```
Mock MVC - Allows you to test controller endpoints during an integration test

`@AutoConfigureMockMvc` annotation needs to be added on top of the integration class

```
@SpringBootTest // makes sure that we run this class the entire application will start up
@AutoConfigureMockMvc
public class PaymentsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

 ....
}
```
