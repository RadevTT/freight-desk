package bg.freightdesk.mapper;

import bg.freightdesk.dto.CarrierDTO;
import bg.freightdesk.entity.Carrier;
import bg.freightdesk.entity.Company;
import bg.freightdesk.service.CompanyService;
import org.springframework.stereotype.Component;

@Component
public class CarrierMapper {

    private final CompanyService companyService;

    public CarrierMapper(CompanyService companyService) {
        this.companyService = companyService;
    }

    public CarrierDTO toDTO(Carrier carrier) {

        CarrierDTO dto = new CarrierDTO();

        dto.setId(carrier.getId());
        dto.setName(carrier.getName());
        dto.setEmail(carrier.getEmail());
        dto.setPhone(carrier.getPhone());
        dto.setCountry(carrier.getCountry());
        dto.setCity(carrier.getCity());

        if (carrier.getCompany() != null) {
            dto.setCompanyId(carrier.getCompany().getId());
        }

        return dto;
    }

    public Carrier toEntity(CarrierDTO dto) {

        Carrier carrier = new Carrier();

        carrier.setId(dto.getId());
        carrier.setName(dto.getName());
        carrier.setEmail(dto.getEmail());
        carrier.setPhone(dto.getPhone());
        carrier.setCountry(dto.getCountry());
        carrier.setCity(dto.getCity());

        if (dto.getCompanyId() != null) {

            Company company =
                    companyService.findEntityById(dto.getCompanyId());

            carrier.setCompany(company);
        }

        return carrier;
    }
}

