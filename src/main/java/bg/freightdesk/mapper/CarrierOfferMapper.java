package bg.freightdesk.mapper;

import bg.freightdesk.dto.CarrierOfferDTO;
import bg.freightdesk.entity.Carrier;
import bg.freightdesk.entity.CarrierOffer;
import bg.freightdesk.entity.Company;
import bg.freightdesk.entity.FreightRequest;
import bg.freightdesk.repository.CarrierRepository;
import bg.freightdesk.repository.CompanyRepository;
import bg.freightdesk.repository.FreightRequestRepository;
import org.springframework.stereotype.Component;

@Component
public class CarrierOfferMapper {

    private final CarrierRepository carrierRepository;
    private final CompanyRepository companyRepository;
    private final FreightRequestRepository freightRequestRepository;

    public CarrierOfferMapper(
            CarrierRepository carrierRepository,
            CompanyRepository companyRepository,
            FreightRequestRepository freightRequestRepository) {

        this.carrierRepository = carrierRepository;
        this.companyRepository = companyRepository;
        this.freightRequestRepository = freightRequestRepository;
    }

    public CarrierOfferDTO toDTO(CarrierOffer entity) {

        CarrierOfferDTO dto = new CarrierOfferDTO();

        dto.setId(
                entity.getId()
        );

        dto.setFreightRequestId(
                entity.getFreightRequest().getId()
        );

        dto.setCompanyId(
                entity.getCompany().getId()
        );

        if (entity.getCarrier() != null) {
            dto.setCarrierId(
                    entity.getCarrier().getId()
            );
        }

        dto.setOfferedPrice(
                entity.getOfferedPrice()
        );

        dto.setCurrency(
                entity.getCurrency()
        );

        dto.setOfferDate(
                entity.getOfferDate()
        );

        dto.setNote(
                entity.getNote()
        );

        dto.setSelected(
                entity.isSelected()
        );

        return dto;
    }

    public CarrierOffer toEntity(CarrierOfferDTO dto) {

        CarrierOffer entity = new CarrierOffer();

        FreightRequest freightRequest =
                freightRequestRepository.findById(
                        dto.getFreightRequestId()
                ).orElseThrow(() ->
                        new RuntimeException("Freight request not found")
                );

        Company company =
                companyRepository.findById(
                        dto.getCompanyId()
                ).orElseThrow(() ->
                        new RuntimeException("Company not found")
                );

        Carrier carrier = null;

        if (dto.getCarrierId() != null) {
            carrier =
                    carrierRepository.findById(
                            dto.getCarrierId()
                    ).orElseThrow(() ->
                            new RuntimeException("Carrier not found")
                    );
        }

        entity.setFreightRequest(freightRequest);
        entity.setCompany(company);
        entity.setCarrier(carrier);
        entity.setOfferedPrice(dto.getOfferedPrice());
        entity.setCurrency(dto.getCurrency());
        entity.setOfferDate(dto.getOfferDate());
        entity.setNote(dto.getNote());
        entity.setSelected(dto.isSelected());

        return entity;
    }
}