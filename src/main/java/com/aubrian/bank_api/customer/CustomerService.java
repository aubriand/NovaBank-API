package com.aubrian.bank_api.customer;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.aubrian.bank_api.customer.dto.CreateCustomerRequest;
import com.aubrian.bank_api.customer.dto.CustomerResponse;
import com.aubrian.bank_api.exception.CustomerNotFoundException;

@Service
public class CustomerService {
  
  private final CustomerRepository customerRepository;
  
  public CustomerService(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  public CustomerResponse create(CreateCustomerRequest createCustomerRequest) {
    Customer newCustomer = new Customer(
      createCustomerRequest.email(),
      createCustomerRequest.firstName(),
      createCustomerRequest.lastName(),
      createCustomerRequest.address(),
      createCustomerRequest.country(),
      createCustomerRequest.postalCode()
    );
    Customer customer = customerRepository.save(newCustomer);
    return getResponse(customer);
  }

  public CustomerResponse getById(UUID uuid) {
    Customer customer = customerRepository.findById(uuid).orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
    return getResponse(customer);
  }

  private CustomerResponse getResponse(Customer customer) {
    CustomerResponse response = new CustomerResponse(
      customer.getId(), 
      customer.getEmail(),
      customer.getFirstName(), 
      customer.getLastName(), 
      customer.getAddress(), 
      customer.getCountry(), 
      customer.getPostalCode()
    );
    return response;
  }
}
