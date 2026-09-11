package bg.freightdesk.mapper;

import bg.freightdesk.dto.CustomerDTO;
import bg.freightdesk.entity.Company;
import bg.freightdesk.entity.Customer;
import bg.freightdesk.service.CompanyService;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    private final CompanyService companyService;

    public CustomerMapper(CompanyService companyService) {
        this.companyService = companyService;
    }

    public CustomerDTO toDTO(Customer customer) {

        CustomerDTO dto = new CustomerDTO();

        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setActive(customer.isActive());

        if (customer.getCompany() != null) {

            dto.setCompanyId(
                    customer.getCompany().getId()
            );

            dto.setCompanyName(
                    customer.getCompany().getName()
            );

            dto.setCompanyCountry(
                    customer.getCompany().getCountry()
            );

            dto.setCompanyCity(
                    customer.getCompany().getCity()
            );
        }

        return dto;
    }

    public Customer toEntity(CustomerDTO dto) {

        Customer customer = new Customer();

        customer.setId(dto.getId());
        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setActive(dto.isActive());

        if (dto.getCompanyId() != null) {

            Company company =
                    companyService.findEntityById(dto.getCompanyId());

            customer.setCompany(company);
        }

        return customer;
    }
}

