package com.amigoscode.testing.customer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Entity
@JsonIgnoreProperties(allowGetters = true) // ignore id coming from the client
public class Customer {
    @Id
    private UUID id;

    @NotBlank // want the client to send us the name
    @Column(nullable = false) // wont allow persistence if property is null
    private String name;

    @NotBlank
    @Column(nullable = false, unique = true) // wont allow persistence if property is null
    private String phoneNumber;

    public Customer() {
    }

    public Customer(UUID id, String name, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override public String toString() {
        return "Customer{" + "id=" + id + ", name='" + name + '\'' + ", phoneNumber='" + phoneNumber
            + '\'' + '}';
    }
}
