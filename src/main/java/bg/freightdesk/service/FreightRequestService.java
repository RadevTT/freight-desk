package bg.freightdesk.service;

import bg.freightdesk.dto.FreightRequestDTO;
import bg.freightdesk.entity.CarrierOffer;
import bg.freightdesk.model.RequestStatus;
import bg.freightdesk.entity.FreightRequest;
import bg.freightdesk.mapper.FreightRequestMapper;
import bg.freightdesk.repository.CarrierOfferRepository;
import bg.freightdesk.repository.FreightRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class FreightRequestService {

    private final FreightRequestRepository freightRequestRepository;
    private final FreightRequestMapper freightRequestMapper;
    private final CarrierOfferRepository carrierOfferRepository;

    public FreightRequestService(
            FreightRequestRepository freightRequestRepository,
            FreightRequestMapper freightRequestMapper,
            CarrierOfferRepository carrierOfferRepository) {

        this.freightRequestRepository = freightRequestRepository;
        this.freightRequestMapper = freightRequestMapper;
        this.carrierOfferRepository = carrierOfferRepository;
    }

    public List<FreightRequestDTO> findAll() {

        return freightRequestRepository.findAll()
                .stream()
                .map(freightRequestMapper::toDTO)
                .toList();
    }

    public FreightRequestDTO findDTOById(UUID id) {

        FreightRequest freightRequest =
                freightRequestRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Freight request not found"
                                )
                        );

        return freightRequestMapper.toDTO(freightRequest);
    }

    @Transactional
    public void saveDTO(FreightRequestDTO dto) {

        FreightRequest freightRequest =
                freightRequestMapper.toEntity(dto);

        /*
         * NEW FREIGHT REQUEST
         *
         * When dto.getId() == null, this is a new request.
         * Carrier and carrier price can be entered manually.
         */
        if (dto.getId() == null) {

            calculateProfit(freightRequest);

            freightRequestRepository.save(freightRequest);

            return;
        }

        /*
         * EXISTING FREIGHT REQUEST
         */
        CarrierOffer selectedOffer =
                carrierOfferRepository
                        .findByFreightRequestIdAndSelectedTrue(
                                dto.getId()
                        );

        /*
         * If there is a selected carrier offer,
         * it is the source of truth for carrier and carrier price.
         */
        if (selectedOffer != null) {

            freightRequest.setCarrier(
                    selectedOffer.getCarrier()
            );

            freightRequest.setCarrierPrice(
                    selectedOffer.getOfferedPrice()
            );

            freightRequest.setStatus(
                    RequestStatus.CONFIRMED
            );
        }

        calculateProfit(freightRequest);

        freightRequestRepository.save(freightRequest);
    }

    private void calculateProfit(
            FreightRequest freightRequest) {

        if (freightRequest.getClientPrice() != null
                && freightRequest.getCarrierPrice() != null) {

            freightRequest.setProfit(
                    freightRequest.getClientPrice()
                            .subtract(
                                    freightRequest.getCarrierPrice()
                            )
            );

        } else {

            freightRequest.setProfit(null);
        }
    }

    @Transactional
    public void delete(UUID id) {

        carrierOfferRepository.deleteByFreightRequestId(id);

        freightRequestRepository.deleteById(id);
    }

    @Transactional
    public void selectCarrierOffer(UUID offerId) {

        CarrierOffer offer =
                carrierOfferRepository.findById(offerId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Carrier offer not found"
                                )
                        );

        FreightRequest freightRequest =
                offer.getFreightRequest();

        freightRequest.setCarrier(
                offer.getCarrier()
        );

        freightRequest.setCarrierPrice(
                offer.getOfferedPrice()
        );

        if (freightRequest.getClientPrice() != null
                && offer.getOfferedPrice() != null) {

            freightRequest.setProfit(
                    freightRequest.getClientPrice()
                            .subtract(
                                    offer.getOfferedPrice()
                            )
            );

        } else {

            freightRequest.setProfit(null);
        }

        freightRequest.setStatus(
                RequestStatus.CONFIRMED
        );

        freightRequestRepository.save(freightRequest);
    }

    @Transactional
    public void deleteCarrierOffer(UUID offerId) {

        CarrierOffer offer =
                carrierOfferRepository.findById(offerId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Carrier offer not found"
                                )
                        );

        FreightRequest freightRequest =
                offer.getFreightRequest();

        if (offer.isSelected()) {

            freightRequest.setCarrier(null);
            freightRequest.setCarrierPrice(null);
            freightRequest.setProfit(null);
            freightRequest.setStatus(
                    RequestStatus.WAITING_FOR_RESPONSE
            );

            freightRequestRepository.save(freightRequest);
        }

        carrierOfferRepository.delete(offer);
    }
}