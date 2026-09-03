package bg.freightdesk.service;

import bg.freightdesk.dto.CarrierDTO;
import bg.freightdesk.entity.Carrier;
import bg.freightdesk.mapper.CarrierMapper;
import bg.freightdesk.repository.CarrierRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CarrierService {

    private final CarrierRepository carrierRepository;
    private final CarrierMapper carrierMapper;

    public CarrierService(
            CarrierRepository carrierRepository,
            CarrierMapper carrierMapper) {

        this.carrierRepository = carrierRepository;
        this.carrierMapper = carrierMapper;
    }

    public List<CarrierDTO> findAll() {

        return carrierRepository.findAll()
                .stream()
                .map(carrierMapper::toDTO)
                .toList();
    }

    public CarrierDTO findDTOById(UUID id) {

        Carrier carrier = carrierRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Carrier not found"));

        return carrierMapper.toDTO(carrier);
    }

    public void saveDTO(CarrierDTO dto) {

        Carrier carrier =
                carrierMapper.toEntity(dto);

        carrierRepository.save(carrier);
    }

    public void delete(UUID id) {

        carrierRepository.deleteById(id);
    }
}