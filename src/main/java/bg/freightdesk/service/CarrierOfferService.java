package bg.freightdesk.service;

import bg.freightdesk.dto.CarrierOfferDTO;
import bg.freightdesk.entity.CarrierOffer;
import bg.freightdesk.mapper.CarrierOfferMapper;
import bg.freightdesk.repository.CarrierOfferRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CarrierOfferService {

    private final CarrierOfferRepository carrierOfferRepository;
    private final CarrierOfferMapper carrierOfferMapper;

    public CarrierOfferService(
            CarrierOfferRepository carrierOfferRepository,
            CarrierOfferMapper carrierOfferMapper) {

        this.carrierOfferRepository = carrierOfferRepository;
        this.carrierOfferMapper = carrierOfferMapper;
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

    public void saveDTO(CarrierOfferDTO dto) {

        CarrierOffer carrierOffer =
                carrierOfferMapper.toEntity(dto);

        carrierOfferRepository.save(carrierOffer);
    }

    public void delete(UUID id) {

        carrierOfferRepository.deleteById(id);
    }

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