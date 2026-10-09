package bg.freightdesk.service;

import bg.freightdesk.dto.CarrierOfferDTO;
import bg.freightdesk.entity.Carrier;
import bg.freightdesk.entity.CarrierOffer;
import bg.freightdesk.entity.Company;
import bg.freightdesk.mapper.CarrierOfferMapper;
import bg.freightdesk.model.RequestStatus;
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

    @Transactional
    public void saveDTO(CarrierOfferDTO dto) {

        CarrierOffer carrierOffer =
                carrierOfferMapper.toEntity(dto);

        CarrierOffer savedOffer =
                carrierOfferRepository.save(carrierOffer);

        /*
         * If the new offer is selected immediately,
         * it becomes the confirmed offer.
         */
        if (savedOffer.isSelected()) {

            freightRequestService.selectCarrierOffer(
                    savedOffer.getId()
            );

            return;
        }

        /*
         * If another offer is already selected,
         * the request remains CONFIRMED.
         *
         * Otherwise the request becomes OFFER_SENT.
         */
        CarrierOffer selectedOffer =
                carrierOfferRepository
                        .findByFreightRequestIdAndSelectedTrue(
                                savedOffer.getFreightRequest().getId()
                        );

        if (selectedOffer == null) {

            freightRequestService.updateStatus(
                    savedOffer.getFreightRequest().getId(),
                    RequestStatus.OFFER_SENT
            );
        }
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
         * If this offer is selected, the freight request
         * immediately receives its new carrier and price.
         */
        if (carrierOffer.isSelected()) {

            freightRequestService.selectCarrierOffer(
                    carrierOffer.getId()
            );

        } else {

            /*
             * If another offer is already selected,
             * the request stays CONFIRMED.
             *
             * Otherwise it remains OFFER_SENT.
             */
            CarrierOffer selectedOffer =
                    carrierOfferRepository
                            .findByFreightRequestIdAndSelectedTrue(
                                    carrierOffer.getFreightRequest().getId()
                            );

            if (selectedOffer == null) {

                freightRequestService.updateStatus(
                        carrierOffer.getFreightRequest().getId(),
                        RequestStatus.OFFER_SENT
                );
            }
        }
    }

    @Transactional
    public void delete(UUID id) {

        CarrierOffer offer =
                carrierOfferRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Carrier offer not found"
                                )
                        );

        freightRequestService.deleteCarrierOffer(id);
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

        /*
         * The selected offer becomes the source of truth
         * for carrier, carrier price, profit and status.
         */
        freightRequestService.selectCarrierOffer(
                offerId
        );
    }
}