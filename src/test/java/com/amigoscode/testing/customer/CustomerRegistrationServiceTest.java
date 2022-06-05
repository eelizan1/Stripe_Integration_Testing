package com.amigoscode.testing.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

class CustomerRegistrationServiceTest {

    @Mock // mock the repo
    private CustomerRepository customerRepository;

    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;

    private CustomerRegistrationService customerRegistrationService;

    @BeforeEach
    void setUp() {
        // initialize all the mocks in this class
        MockitoAnnotations.initMocks(this);
        // before each test have a fresh new instance CustomerRegistrationService
        customerRegistrationService = new CustomerRegistrationService(customerRepository);
    }

    @Test
    void itShouldSaveNewCustomer() {
        // Given
        String phoneNumber = "000099";
        Customer customer = new Customer(UUID.randomUUID(), "Maryam", phoneNumber);
        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest(customer);

        // Test if no customer already exists - No customer with phone number passed - will return empty
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(
            Optional.empty());

        // When
        customerRegistrationService.registerNewCustomer(registrationRequest);

        // Then
        then(customerRepository).should().save(customerArgumentCaptor.capture()); // capture the customer argument being saved in the service
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue(); // extract the customer object
        assertThat(customerArgumentCaptorValue).isEqualToComparingFieldByField(customer); // compare with expected result;
    }

    @Test
    void itShouldSaveNewCustomerWhenIdIsNull() {
        // Given
        String phoneNumber = "000099";
        Customer customer = new Customer(null, "Maryam", phoneNumber);
        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest(customer);

        // Test if no customer already exists - No customer with phone number passed - will return empty
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(
            Optional.empty());

        // When
        customerRegistrationService.registerNewCustomer(registrationRequest);

        // Then
        then(customerRepository).should().save(customerArgumentCaptor.capture()); // capture the customer argument being saved in the service
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue(); // extract the customer object
        assertThat(customerArgumentCaptorValue).isEqualToIgnoringGivenFields(customer, "id"); // compare with expected result;
        assertThat(customerArgumentCaptorValue.getId()).isNotNull();
    }

    @Test
    void itShouldNotSaveCustomerWhenCustomerExists() {
        // Given
        String phoneNumber = "000099";
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "Maryam", phoneNumber);
        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest(customer);

        // Test if customer already exists
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
            .willReturn(Optional.of(customer));

        // When
        customerRegistrationService.registerNewCustomer(registrationRequest);

        // Then
        then(customerRepository).should(never()).save(any()); // if we have an existing customer then repo should not save
        // .. OR test if repo wont have any interactions
        then(customerRepository).should().selectCustomerByPhoneNumber(phoneNumber); // mock had first interaction by checking the phone number
        then(customerRepository).shouldHaveNoMoreInteractions(); // but wont have anymore since customer exists
    }

    @Test
    void itShouldThrowExceptionWhenNamesDontMatch() {
        // Given
        String phoneNumber = "000099";
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "Maryam", phoneNumber);
        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest(customer);

        Customer customer2 = new Customer(UUID.randomUUID(), "Alex", phoneNumber);

        // Test if customer already exists
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
            .willReturn(Optional.of(customer2));

        // When
        assertThatThrownBy(() -> customerRegistrationService.registerNewCustomer(registrationRequest))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining(String.format("phone number [%s] is taken", phoneNumber));

        // Finally
        then(customerRepository).should(never()).save(any(Customer.class)); // repo should not be saving anything
    }
}
