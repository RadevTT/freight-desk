package bg.freightdesk.service;

import bg.freightdesk.dto.CustomerDTO;
import bg.freightdesk.entity.Customer;
import bg.freightdesk.mapper.CustomerMapper;
import bg.freightdesk.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerService(
            CustomerRepository customerRepository,
            CustomerMapper customerMapper) {

        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    public List<CustomerDTO> findAll() {

        return customerRepository
                .findAllByActiveTrueAndCompany_ActiveTrue()
                .stream()
                .map(customerMapper::toDTO)
                .toList();
    }

    public List<CustomerDTO> findAllForEdit(UUID currentId) {

        List<CustomerDTO> customers =
                new ArrayList<>(findAll());

        if (currentId != null) {

            CustomerDTO currentCustomer =
                    findDTOById(currentId);

            if (!currentCustomer.isActive()) {
                customers.add(currentCustomer);
            }
        }

        return customers;
    }

    public List<CustomerDTO> findInactive() {

        return customerRepository.findAllByActiveFalse()
                .stream()
                .map(customerMapper::toDTO)
                .toList();
    }

    public CustomerDTO findDTOById(UUID id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Customer not found"));

        return customerMapper.toDTO(customer);
    }

    public Customer findById(UUID id) {

        return customerRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Customer not found"));
    }

    public void saveDTO(CustomerDTO dto) {

        Customer customer =
                customerMapper.toEntity(dto);

        customerRepository.save(customer);
    }

    public void deactivate(UUID id) {

        Customer customer =
                customerRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Customer not found"));

        customer.setActive(false);

        customerRepository.save(customer);
    }

    public void activate(UUID id) {

        Customer customer =
                customerRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Customer not found"));

        customer.setActive(true);

        customerRepository.save(customer);
    }

    public List<CustomerDTO> findAllByCompany(UUID companyId) {
        return customerRepository
                .findAllByCompany_IdAndActiveTrue(companyId)
                .stream()
                .map(customerMapper::toDTO)
                .toList();
    }

    public List<CustomerDTO> findInactiveByCompany(UUID companyId) {

        return customerRepository
                .findAllByCompany_IdAndActiveFalse(companyId)
                .stream()
                .map(customerMapper::toDTO)
                .toList();
    }
}