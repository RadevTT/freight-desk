package bg.freightdesk.service;

import bg.freightdesk.dto.FreightRequestDTO;
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

        FreightRequest freightRequest = freightRequestRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Freight request not found"));

        return freightRequestMapper.toDTO(freightRequest);
    }

    public void saveDTO(FreightRequestDTO dto) {

        FreightRequest freightRequest =
                freightRequestMapper.toEntity(dto);

        freightRequestRepository.save(freightRequest);
    }

    @Transactional
    public void delete(UUID id) {

        carrierOfferRepository.deleteByFreightRequestId(id);

        freightRequestRepository.deleteById(id);
    }
}