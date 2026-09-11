package bg.freightdesk.service;

import bg.freightdesk.dto.CarrierDTO;
import bg.freightdesk.entity.Carrier;
import bg.freightdesk.mapper.CarrierMapper;
import bg.freightdesk.repository.CarrierRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

        return carrierRepository.findAllByActiveTrue()
                .stream()
                .map(carrierMapper::toDTO)
                .toList();
    }

    public List<CarrierDTO> findAllForEdit(UUID currentId) {

        List<CarrierDTO> carriers =
                new ArrayList<>(findAll());

        if (currentId != null) {

            CarrierDTO currentCarrier =
                    findDTOById(currentId);

            if (!currentCarrier.isActive()) {
                carriers.add(currentCarrier);
            }
        }

        return carriers;
    }

    public List<CarrierDTO> findInactive() {

        return carrierRepository.findAllByActiveFalse()
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

    public Carrier findById(UUID id) {

        return carrierRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Carrier not found"));
    }

    public void saveDTO(CarrierDTO dto) {

        Carrier carrier =
                carrierMapper.toEntity(dto);

        carrierRepository.save(carrier);
    }

    public void deactivate(UUID id) {

        Carrier carrier = carrierRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Carrier not found"));

        carrier.setActive(false);

        carrierRepository.save(carrier);
    }

    public void activate(UUID id) {

        Carrier carrier = carrierRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Carrier not found"));

        carrier.setActive(true);

        carrierRepository.save(carrier);
    }

    public List<CarrierDTO> findAllByCompany(UUID companyId) {

        return carrierRepository
                .findAllByCompany_IdAndActiveTrue(companyId)
                .stream()
                .map(carrierMapper::toDTO)
                .toList();
    }

    public List<CarrierDTO> findAllByCompanyForEdit(
            UUID companyId,
            UUID currentId) {

        List<CarrierDTO> carriers =
                new ArrayList<>(findAllByCompany(companyId));

        if (currentId != null) {

            CarrierDTO currentCarrier =
                    findDTOById(currentId);

            if (!currentCarrier.isActive()) {
                carriers.add(currentCarrier);
            }
        }

        return carriers;
    }

    public List<CarrierDTO> findInactiveByCompany(UUID companyId) {

        return carrierRepository
                .findAllByCompany_IdAndActiveFalse(companyId)
                .stream()
                .map(carrierMapper::toDTO)
                .toList();
    }
}

