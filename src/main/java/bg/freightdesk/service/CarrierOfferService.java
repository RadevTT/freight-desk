package bg.freightdesk.service;

import bg.freightdesk.dto.CarrierOfferDTO;
import bg.freightdesk.entity.Carrier;
import bg.freightdesk.entity.CarrierOffer;
import bg.freightdesk.entity.Company;
import bg.freightdesk.mapper.CarrierOfferMapper;
import bg.freightdesk.repository.CarrierOfferRepository;
import bg.freightdesk.repository.CarrierRepository;
import bg.freightdesk.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CarrierOfferService {

    private final CarrierOfferRepository carrierOfferRepository;
    private final CarrierOfferMapper carrierOfferMapper;
    private final CarrierRepository carrierRepository;
    private final CompanyRepository companyRepository;
    private final FreightRequestService freightRequestService;

    public CarrierOfferService(
            CarrierOfferRepository carrierOfferRepository,
            CarrierOfferMapper carrierOfferMapper,
            CarrierRepository carrierRepository,
            CompanyRepository companyRepository,
            FreightRequestService freightRequestService) {

        this.carrierOfferRepository = carrierOfferRepository;
        this.carrierOfferMapper = carrierOfferMapper;
        this.carrierRepository = carrierRepository;
        this.companyRepository = companyRepository;
        this.freightRequestService = freightRequestService;
    }

    public List<CarrierOfferDTO> findAll() {

        return carrierOfferRepository.findAll()
                .stream()
                .map(carrierOfferMapper::toDTO)
                .toList();
    }

    public List<CarrierOfferDTO> findByFreightRequestId(
            UUID freightRequestId) {

        return carrierOfferRepository
                .findByFreightRequestId(freightRequestId)
                .stream()
                .map(carrierOfferMapper::toDTO)
                .toList();
    }

    public CarrierOfferDTO findDTOById(UUID id) {

        CarrierOffer carrierOffer =
                carrierOfferRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Carrier offer not found"
                                )
                        );

        return carrierOfferMapper.toDTO(carrierOffer);
    }

    public CarrierOfferDTO findSelectedByFreightRequestId(
            UUID freightRequestId) {

        CarrierOffer offer =
                carrierOfferRepository
                        .findByFreightRequestIdAndSelectedTrue(
                                freightRequestId
                        );

        if (offer == null) {
            return null;
        }

        return carrierOfferMapper.toDTO(offer);
    }

    public void saveDTO(CarrierOfferDTO dto) {

        CarrierOffer carrierOffer =
                carrierOfferMapper.toEntity(dto);

        carrierOfferRepository.save(carrierOffer);
    }

    @Transactional
    public void updateDTO(CarrierOfferDTO dto) {

        CarrierOffer carrierOffer =
                carrierOfferRepository.findById(dto.getId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Carrier offer not found"
                                )
                        );

        Company company =
                companyRepository.findById(dto.getCompanyId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Company not found"
                                )
                        );

        Carrier carrier = null;

        if (dto.getCarrierId() != null) {

            carrier =
                    carrierRepository.findById(dto.getCarrierId())
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Carrier not found"
                                    )
                            );
        }

        carrierOffer.setCompany(company);
        carrierOffer.setCarrier(carrier);
        carrierOffer.setOfferedPrice(dto.getOfferedPrice());
        carrierOffer.setCurrency(dto.getCurrency());
        carrierOffer.setNote(dto.getNote());

        carrierOfferRepository.save(carrierOffer);

        /*
         * If this offer is selected,
         * Freight Request must immediately
         * receive the new carrier and price.
         */
        if (carrierOffer.isSelected()) {

            freightRequestService.selectCarrierOffer(
                    carrierOffer.getId()
            );
        }
    }

    public void delete(UUID id) {

        carrierOfferRepository.deleteById(id);
    }

    @Transactional
    public void selectOffer(UUID offerId) {

        CarrierOffer selectedOffer =
                carrierOfferRepository.findById(offerId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Carrier offer not found"
                                )
                        );

        UUID freightRequestId =
                selectedOffer.getFreightRequest().getId();

        List<CarrierOffer> offers =
                carrierOfferRepository.findByFreightRequestId(
                        freightRequestId
                );

        for (CarrierOffer offer : offers) {

            offer.setSelected(
                    offer.getId().equals(offerId)
            );
        }

        carrierOfferRepository.saveAll(offers);
    }


}
