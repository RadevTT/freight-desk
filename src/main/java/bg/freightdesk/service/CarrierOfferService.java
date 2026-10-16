
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
import java.time.LocalDateTime;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

    /**
     * Връща офертите само за заявки, които текущият потребител
     * има право да вижда.
     */
    @Transactional(readOnly = true)
    public List<CarrierOfferDTO> findAll() {

        Set<UUID> accessibleRequestIds = freightRequestService.findAll()
                .stream()
                .map(request -> request.getId())
                .collect(Collectors.toSet());

        return carrierOfferRepository.findAll()
                .stream()
                .filter(offer ->
                        accessibleRequestIds.contains(
                                offer.getFreightRequest().getId()))
                .map(carrierOfferMapper::toDTO)
                .toList();
    }

    /**
     * Връща офертите към конкретна заявка след проверка
     * дали потребителят има право да я вижда.
     */
    @Transactional(readOnly = true)
    public List<CarrierOfferDTO> findByFreightRequestId(
            UUID freightRequestId) {

        freightRequestService.requireCanView(freightRequestId);

        return carrierOfferRepository
                .findByFreightRequestId(freightRequestId)
                .stream()
                .map(carrierOfferMapper::toDTO)
                .toList();
    }

    /**
     * Връща една оферта само ако потребителят има право
     * да вижда заявката, към която принадлежи.
     */
    @Transactional(readOnly = true)
    public CarrierOfferDTO findDTOById(UUID id) {

        CarrierOffer carrierOffer = carrierOfferRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Carrier offer not found"));

        UUID requestId = carrierOffer.getFreightRequest().getId();

        freightRequestService.requireCanView(requestId);

        return carrierOfferMapper.toDTO(carrierOffer);
    }

    /**
     * Връща избраната оферта за заявка.
     */
    @Transactional(readOnly = true)
    public CarrierOfferDTO findSelectedByFreightRequestId(
            UUID freightRequestId) {

        freightRequestService.requireCanView(freightRequestId);

        CarrierOffer offer = carrierOfferRepository
                .findByFreightRequestIdAndSelectedTrue(freightRequestId);

        if (offer == null) {
            return null;
        }

        return carrierOfferMapper.toDTO(offer);
    }

    /**
     * Добавя нова оферта.
     * VIEW_AND_ADD, FULL_ACCESS и ADMIN могат да добавят
     * според правилата, наложени от FreightRequestService.
     */
    @Transactional
    public void saveDTO(CarrierOfferDTO dto) {

        if (dto.getFreightRequestId() == null) {
            throw new IllegalArgumentException(
                    "Freight request ID is required");
        }

        freightRequestService.requireCanAddTo(
                dto.getFreightRequestId());

        CarrierOffer carrierOffer = carrierOfferMapper.toEntity(dto);

        if (carrierOffer.getOfferDate() == null) {
    carrierOffer.setOfferDate(LocalDateTime.now());
}

        validateCarrierCompany(
        carrierOffer.getCompany(),
        carrierOffer.getCarrier());

        CarrierOffer savedOffer =
                carrierOfferRepository.save(carrierOffer);

        if (savedOffer.isSelected()) {
            freightRequestService.selectCarrierOffer(
                    savedOffer.getId());
            return;
        }

        freightRequestService.markOfferSentIfNoSelectedOffer(
                savedOffer.getFreightRequest().getId());
    }

    /**
     * Редактира съществуваща оферта.
     * VIEW_AND_ADD и FULL_ACCESS могат да редактират.
     */
    @Transactional
    public void updateDTO(CarrierOfferDTO dto) {

        CarrierOffer carrierOffer =
                carrierOfferRepository.findById(dto.getId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Carrier offer not found"));

        UUID requestId =
                carrierOffer.getFreightRequest().getId();

        freightRequestService.requireCanAddTo(requestId);

        if (dto.getCompanyId() == null) {
            throw new IllegalArgumentException(
                    "Carrier company ID is required");
        }

        Company company = companyRepository.findById(
                        dto.getCompanyId())
                .orElseThrow(() ->
                        new RuntimeException("Company not found"));

        Carrier carrier = null;

        if (dto.getCarrierId() != null) {
            carrier = carrierRepository.findById(
                            dto.getCarrierId())
                    .orElseThrow(() ->
                            new RuntimeException("Carrier not found"));
        }

        validateCarrierCompany(company, carrier);

        carrierOffer.setCompany(company);
        carrierOffer.setCarrier(carrier);
        carrierOffer.setOfferedPrice(dto.getOfferedPrice());
        carrierOffer.setCurrency(dto.getCurrency());
        carrierOffer.setNote(dto.getNote());

        carrierOfferRepository.save(carrierOffer);

        if (carrierOffer.isSelected()) {
            freightRequestService.selectCarrierOffer(
                    carrierOffer.getId());
        } else {
            freightRequestService.markOfferSentIfNoSelectedOffer(
                    requestId);
        }
    }


    /**
     * Проверява дали фирмата и превозвачът са валидни.
     */
    private void validateCarrierCompany(Company company, Carrier carrier) {

        if (company.getType() != bg.freightdesk.model.CompanyType.CARRIER) {
            throw new IllegalArgumentException(
                    "The selected company must be a carrier company");
        }

        if (!company.isActive()) {
            throw new IllegalArgumentException(
                    "The selected carrier company is inactive");
        }

        if (carrier != null) {
            if (!carrier.isActive()) {
                throw new IllegalArgumentException(
                        "The selected carrier is inactive");
            }

            if (carrier.getCompany() == null
                    || !carrier.getCompany().getId().equals(company.getId())) {
                throw new IllegalArgumentException(
                        "The selected carrier does not belong to the selected company");
            }
        }
    }


    /**
     * Изтрива оферта.
     * Реалната проверка за право на изтриване се извършва
     * във FreightRequestService.
     */
    @Transactional
    public void delete(UUID id) {
        freightRequestService.deleteCarrierOffer(id);
    }

    /**
     * Избира оферта и актуализира заявката.
     */
    @Transactional
    public void selectOffer(UUID offerId) {

        CarrierOffer selectedOffer =
                carrierOfferRepository.findById(offerId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Carrier offer not found"));

        UUID freightRequestId =
                selectedOffer.getFreightRequest().getId();

        freightRequestService.requireCanAddTo(freightRequestId);

        List<CarrierOffer> offers =
                carrierOfferRepository.findByFreightRequestId(
                        freightRequestId);

        for (CarrierOffer offer : offers) {
            offer.setSelected(offer.getId().equals(offerId));
        }

        carrierOfferRepository.saveAll(offers);

        freightRequestService.selectCarrierOffer(offerId);
    }
}
