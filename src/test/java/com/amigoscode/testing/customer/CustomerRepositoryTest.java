package com.amigoscode.testing.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest( // allows us to test database queries
    properties = {
        "spring.jpa.properties.javax.persistence.validation.mode=none" // will trigger jpa annotations (@Column(nullable = false) when unit testing
    }
)
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void itShouldSelectCustomerByPhoneNumber() {
        // Given
        UUID id = UUID.randomUUID();
        String phoneNumber = "404-291-3891";
        Customer customer = new Customer(id, "Abel", phoneNumber);

        // When
        customerRepository.save(customer);

        // Then
        Optional<Customer> customerOptional = customerRepository.selectCustomerByPhoneNumber(phoneNumber);
        assertThat(customerOptional)
            .isPresent()
            .hasValueSatisfying(c -> {
                assertThat(c).isEqualToComparingFieldByField(customer);
            });
    }

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

    @Test
    void itShouldNotSelectCustomerByPhoneNumberWhenPhoneNumberDoesNotExist() {
        // Given
        String phoneNumber = "0000";

        // When
        Optional<Customer> optionalCustomer = customerRepository.selectCustomerByPhoneNumber(phoneNumber);

        // Then
        assertThat(optionalCustomer).isNotPresent();
    }
    @Test
    void itShouldNotSaveCustomerWhenNameIsNull() {
        // Given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, null, "0000");

        // When
        // Then
        assertThatThrownBy(() -> customerRepository.save(customer))
            // expected values (message and exception class) found from logs
            .hasMessageContaining("not-null property references a null or transient value : com.amigoscode.testing.customer.Customer.name")
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void itShouldNotSaveCustomerWhenPhoneNumberIsNull() {
        // Given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "Alex", null);

        // When
        // Then
        assertThatThrownBy(() -> customerRepository.save(customer))
            .hasMessageContaining("not-null property references a null or transient value : com.amigoscode.testing.customer.Customer.phoneNumber")
            .isInstanceOf(DataIntegrityViolationException.class);

    }
}
