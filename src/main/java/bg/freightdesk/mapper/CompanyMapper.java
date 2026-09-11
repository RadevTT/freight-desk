package bg.freightdesk.mapper;

import bg.freightdesk.dto.CompanyDTO;
import bg.freightdesk.entity.Company;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    public CompanyDTO toDTO(Company company) {

        CompanyDTO dto = new CompanyDTO();

        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setCompanyCode(company.getCompanyCode());
        dto.setType(company.getType());
        dto.setCountry(company.getCountry());
        dto.setCity(company.getCity());
        dto.setActive(company.isActive());

        return dto;
    }

    public Company toEntity(CompanyDTO dto) {

        Company company = new Company();

        company.setId(dto.getId());
        company.setName(dto.getName());
        company.setCompanyCode(dto.getCompanyCode());
        company.setType(dto.getType());
        company.setCountry(dto.getCountry());
        company.setCity(dto.getCity());
        company.setActive(dto.isActive());

        return company;
    }
}