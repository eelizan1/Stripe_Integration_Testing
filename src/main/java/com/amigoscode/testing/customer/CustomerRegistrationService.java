package com.amigoscode.testing.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerRegistrationService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerRegistrationService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void registerNewCustomer(CustomerRegistrationRequest request) {
        // Check if phone number exists
        String phoneNumber = request.getCustomer().getPhoneNumber();
        Optional<Customer> customerOptional = customerRepository.selectCustomerByPhoneNumber(phoneNumber);

        // if taken lets check if it belongs to same customer
        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            if (customer.getName().equals(request.getCustomer().getName())) {
                return; // no need to do anything since customer exists
            }
            // throw error if phone number is taken
            throw new IllegalStateException(String.format("phone number [%s] is taken", phoneNumber));
        }

        if (request.getCustomer().getId() == null) {
            request.getCustomer().setId(UUID.randomUUID());
        }

        // customer doesnt exist so save to repo
        customerRepository.save(request.getCustomer());
    }
}
