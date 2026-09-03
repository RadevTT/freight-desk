package bg.freightdesk.mapper;

import bg.freightdesk.dto.FreightRequestDTO;
import bg.freightdesk.entity.FreightRequest;
import bg.freightdesk.model.RequestStatus;
import bg.freightdesk.repository.CarrierRepository;
import bg.freightdesk.repository.CustomerRepository;
import org.springframework.stereotype.Component;

@Component
public class FreightRequestMapper {

    private final CustomerRepository customerRepository;
    private final CarrierRepository carrierRepository;

    public FreightRequestMapper(
            CustomerRepository customerRepository,
            CarrierRepository carrierRepository) {

        this.customerRepository = customerRepository;
        this.carrierRepository = carrierRepository;
    }

    public FreightRequestDTO toDTO(FreightRequest freightRequest) {

        FreightRequestDTO dto = new FreightRequestDTO();

        dto.setId(freightRequest.getId());

        dto.setRequestDate(freightRequest.getRequestDate());
        dto.setLoadingDate(freightRequest.getLoadingDate());
        dto.setDeliveryTime(freightRequest.getDeliveryTime());

        dto.setLoadingCountry(freightRequest.getLoadingCountry());
        dto.setLoadingCity(freightRequest.getLoadingCity());
        dto.setLoadingPostalCode(
                freightRequest.getLoadingPostalCode()
        );

        dto.setUnloadingCountry(
                freightRequest.getUnloadingCountry()
        );

        dto.setUnloadingCity(
                freightRequest.getUnloadingCity()
        );

        dto.setUnloadingPostalCode(
                freightRequest.getUnloadingPostalCode()
        );

        dto.setWeight(freightRequest.getWeight());
        dto.setLoadingMeters(freightRequest.getLoadingMeters());
        dto.setVolume(freightRequest.getVolume());
        dto.setDimensions(freightRequest.getDimensions());
        dto.setPallets(freightRequest.getPallets());
        dto.setCargoType(freightRequest.getCargoType());

        dto.setClientPrice(freightRequest.getClientPrice());
        dto.setCarrierPrice(freightRequest.getCarrierPrice());

        dto.setSpeditorPrice(
                freightRequest.getSpeditorPrice()
        );

        dto.setAdditionalInfo(
                freightRequest.getAdditionalInfo()
        );

        dto.setProfit(freightRequest.getProfit());

        // ==========================================
        // SPEDITOR OPTIONS
        // ==========================================

        dto.setTrailer(freightRequest.isTrailer());
        dto.setHanger(freightRequest.isHanger());
        dto.setRefrigerated(freightRequest.isRefrigerated());
        dto.setDualTemperature(
                freightRequest.isDualTemperature()
        );
        dto.setVan(freightRequest.isVan());
        dto.setMicrobus(freightRequest.isMicrobus());
        dto.setClothingTruck(
                freightRequest.isClothingTruck()
        );
        dto.setSolo(freightRequest.isSolo());
        dto.setJumbo(freightRequest.isJumbo());
        dto.setTractor(freightRequest.isTractor());
        dto.setPlatform(freightRequest.isPlatform());
        dto.setFlatbed(freightRequest.isFlatbed());
        dto.setContainerTruck(
                freightRequest.isContainerTruck()
        );
        dto.setContainer(freightRequest.isContainer());
        dto.setTanker(freightRequest.isTanker());
        dto.setMegatrailer(
                freightRequest.isMegatrailer()
        );
        dto.setCarCarrier(
                freightRequest.isCarCarrier()
        );
        dto.setGondola(freightRequest.isGondola());
        dto.setOversized(
                freightRequest.isOversized()
        );
        dto.setTrailerOnly(
                freightRequest.isTrailerOnly()
        );
        dto.setTunnel(freightRequest.isTunnel());
        dto.setTailLift(freightRequest.isTailLift());
        dto.setCrane(freightRequest.isCrane());
        dto.setTwoDrivers(
                freightRequest.isTwoDrivers()
        );
        dto.setPalletTruck(
                freightRequest.isPalletTruck()
        );

        if (freightRequest.getCustomer() != null) {

            dto.setCustomerId(
                    freightRequest.getCustomer().getId()
            );
        }

        if (freightRequest.getCarrier() != null) {

            dto.setCarrierId(
                    freightRequest.getCarrier().getId()
            );
        }

        if (freightRequest.getStatus() != null) {

            dto.setStatus(
                    freightRequest.getStatus().name()
            );
        }

        return dto;
    }

    public FreightRequest toEntity(FreightRequestDTO dto) {

        FreightRequest freightRequest =
                new FreightRequest();

        freightRequest.setId(dto.getId());

        freightRequest.setRequestDate(
                dto.getRequestDate()
        );

        freightRequest.setLoadingDate(
                dto.getLoadingDate()
        );

        freightRequest.setDeliveryTime(
                dto.getDeliveryTime()
        );

        freightRequest.setLoadingCountry(
                dto.getLoadingCountry()
        );

        freightRequest.setLoadingCity(
                dto.getLoadingCity()
        );

        freightRequest.setLoadingPostalCode(
                dto.getLoadingPostalCode()
        );

        freightRequest.setUnloadingCountry(
                dto.getUnloadingCountry()
        );

        freightRequest.setUnloadingCity(
                dto.getUnloadingCity()
        );

        freightRequest.setUnloadingPostalCode(
                dto.getUnloadingPostalCode()
        );

        freightRequest.setWeight(dto.getWeight());

        freightRequest.setLoadingMeters(
                dto.getLoadingMeters()
        );

        freightRequest.setVolume(dto.getVolume());

        freightRequest.setDimensions(
                dto.getDimensions()
        );

        freightRequest.setPallets(dto.getPallets());

        freightRequest.setCargoType(
                dto.getCargoType()
        );

        freightRequest.setClientPrice(
                dto.getClientPrice()
        );

        freightRequest.setCarrierPrice(
                dto.getCarrierPrice()
        );

        freightRequest.setSpeditorPrice(
                dto.getSpeditorPrice()
        );

        freightRequest.setAdditionalInfo(
                dto.getAdditionalInfo()
        );

        // ==========================================
        // SPEDITOR OPTIONS
        // ==========================================

        freightRequest.setTrailer(dto.isTrailer());
        freightRequest.setHanger(dto.isHanger());
        freightRequest.setRefrigerated(
                dto.isRefrigerated()
        );

        freightRequest.setDualTemperature(
                dto.isDualTemperature()
        );

        freightRequest.setVan(dto.isVan());
        freightRequest.setMicrobus(dto.isMicrobus());

        freightRequest.setClothingTruck(
                dto.isClothingTruck()
        );

        freightRequest.setSolo(dto.isSolo());
        freightRequest.setJumbo(dto.isJumbo());
        freightRequest.setTractor(dto.isTractor());
        freightRequest.setPlatform(dto.isPlatform());
        freightRequest.setFlatbed(dto.isFlatbed());

        freightRequest.setContainerTruck(
                dto.isContainerTruck()
        );

        freightRequest.setContainer(dto.isContainer());
        freightRequest.setTanker(dto.isTanker());

        freightRequest.setMegatrailer(
                dto.isMegatrailer()
        );

        freightRequest.setCarCarrier(
                dto.isCarCarrier()
        );

        freightRequest.setGondola(dto.isGondola());

        freightRequest.setOversized(
                dto.isOversized()
        );

        freightRequest.setTrailerOnly(
                dto.isTrailerOnly()
        );

        freightRequest.setTunnel(dto.isTunnel());
        freightRequest.setTailLift(dto.isTailLift());
        freightRequest.setCrane(dto.isCrane());

        freightRequest.setTwoDrivers(
                dto.isTwoDrivers()
        );

        freightRequest.setPalletTruck(
                dto.isPalletTruck()
        );

        if (dto.getStatus() != null
                && !dto.getStatus().isBlank()) {

            freightRequest.setStatus(
                    RequestStatus.valueOf(dto.getStatus())
            );
        }

        if (dto.getCustomerId() != null) {

            freightRequest.setCustomer(
                    customerRepository.findById(
                            dto.getCustomerId()
                    ).orElseThrow(() ->
                            new RuntimeException(
                                    "Customer not found"
                            ))
            );
        }

        if (dto.getCarrierId() != null) {

            freightRequest.setCarrier(
                    carrierRepository.findById(
                            dto.getCarrierId()
                    ).orElseThrow(() ->
                            new RuntimeException(
                                    "Carrier not found"
                            ))
            );
        }

        if (dto.getClientPrice() != null
                && dto.getCarrierPrice() != null) {

            freightRequest.setProfit(
                    dto.getClientPrice()
                            .subtract(
                                    dto.getCarrierPrice()
                            )
            );
        }

        return freightRequest;
    }
}