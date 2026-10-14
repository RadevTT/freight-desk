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

                CarrierOffer carrierOffer = carrierOfferRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException(
                                                "Carrier offer not found"));

                return carrierOfferMapper.toDTO(carrierOffer);
        }

        public CarrierOfferDTO findSelectedByFreightRequestId(
                        UUID freightRequestId) {

                CarrierOffer offer = carrierOfferRepository
                                .findByFreightRequestIdAndSelectedTrue(
                                                freightRequestId);

                if (offer == null) {
                        return null;
                }

                return carrierOfferMapper.toDTO(offer);
        }

        @Transactional
        public void saveDTO(CarrierOfferDTO dto) {
                if (dto.getFreightRequestId() == null) {
                        throw new IllegalArgumentException(
                                        "Freight request ID is required");
                }

                freightRequestService.requireCanAddTo(dto.getFreightRequestId());

                CarrierOffer carrierOffer = carrierOfferMapper.toEntity(dto);

                CarrierOffer savedOffer = carrierOfferRepository.save(carrierOffer);

                if (savedOffer.isSelected()) {
                        freightRequestService.requireCanManage(
                                        savedOffer.getFreightRequest().getId());

                        freightRequestService.selectCarrierOffer(savedOffer.getId());
                        return;
                }

                freightRequestService.markOfferSentIfNoSelectedOffer(
                                savedOffer.getFreightRequest().getId());
        }

        @Transactional
        public void updateDTO(CarrierOfferDTO dto) {
                CarrierOffer carrierOffer = carrierOfferRepository.findById(dto.getId())
                                .orElseThrow(() -> new RuntimeException("Carrier offer not found"));

                UUID requestId = carrierOffer.getFreightRequest().getId();

                // Редактирането на съществуваща оферта изисква FULL_ACCESS.
                freightRequestService.requireCanManage(requestId);

                Company company = companyRepository.findById(dto.getCompanyId())
                                .orElseThrow(() -> new RuntimeException("Company not found"));

                Carrier carrier = null;

                if (dto.getCarrierId() != null) {
                        carrier = carrierRepository.findById(dto.getCarrierId())
                                        .orElseThrow(() -> new RuntimeException("Carrier not found"));
                }

                carrierOffer.setCompany(company);
                carrierOffer.setCarrier(carrier);
                carrierOffer.setOfferedPrice(dto.getOfferedPrice());
                carrierOffer.setCurrency(dto.getCurrency());
                carrierOffer.setNote(dto.getNote());

                carrierOfferRepository.save(carrierOffer);

                if (carrierOffer.isSelected()) {
                        freightRequestService.selectCarrierOffer(carrierOffer.getId());
                } else {
                        freightRequestService.markOfferSentIfNoSelectedOffer(requestId);
                }
        }

        @Transactional
        public void delete(UUID id) {
                freightRequestService.deleteCarrierOffer(id);
        }

        @Transactional
        public void selectOffer(UUID offerId) {
                CarrierOffer selectedOffer = carrierOfferRepository.findById(offerId)
                                .orElseThrow(() -> new RuntimeException("Carrier offer not found"));

                UUID freightRequestId = selectedOffer.getFreightRequest().getId();

                // Проверяваме правата преди да променим избраните оферти.
                freightRequestService.requireCanManage(freightRequestId);

                List<CarrierOffer> offers = carrierOfferRepository.findByFreightRequestId(
                                freightRequestId);

                for (CarrierOffer offer : offers) {
                        offer.setSelected(offer.getId().equals(offerId));
                }

                carrierOfferRepository.saveAll(offers);

                freightRequestService.selectCarrierOffer(offerId);
        }

}