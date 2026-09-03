package bg.freightdesk.service;

import bg.freightdesk.dto.CustomerDTO;
import bg.freightdesk.entity.Customer;
import bg.freightdesk.mapper.CustomerMapper;
import bg.freightdesk.repository.CustomerRepository;
import org.springframework.stereotype.Service;

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

        return customerRepository.findAll()
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

    public void saveDTO(CustomerDTO dto) {

        Customer customer =
                customerMapper.toEntity(dto);

        customerRepository.save(customer);
    }

    public void delete(UUID id) {
        customerRepository.deleteById(id);
    }
}