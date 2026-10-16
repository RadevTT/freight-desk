
package bg.freightdesk.service;

import bg.freightdesk.dto.CarrierOfferDTO;
import bg.freightdesk.mapper.CarrierOfferMapper;
import bg.freightdesk.repository.CarrierOfferRepository;
import bg.freightdesk.repository.CarrierRepository;
import bg.freightdesk.repository.CompanyRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CarrierOfferServiceTest {

    private CarrierOfferRepository carrierOfferRepository;
    private CarrierOfferMapper carrierOfferMapper;
    private CarrierRepository carrierRepository;
    private CompanyRepository companyRepository;
    private FreightRequestService freightRequestService;

    private CarrierOfferService carrierOfferService;

    @BeforeEach
    void setUp() {
        carrierOfferRepository = mock(CarrierOfferRepository.class);
        carrierOfferMapper = mock(CarrierOfferMapper.class);
        carrierRepository = mock(CarrierRepository.class);
        companyRepository = mock(CompanyRepository.class);
        freightRequestService = mock(FreightRequestService.class);

        carrierOfferService = new CarrierOfferService(
                carrierOfferRepository,
                carrierOfferMapper,
                carrierRepository,
                companyRepository,
                freightRequestService
        );
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void cannotSaveOfferWhenUserDoesNotHaveAddPermission() {
        UUID requestId = UUID.randomUUID();

        CarrierOfferDTO dto = new CarrierOfferDTO();
        dto.setFreightRequestId(requestId);

        doThrow(new AccessDeniedException("Access denied"))
                .when(freightRequestService)
                .requireCanAddTo(requestId);

        assertThrows(
                AccessDeniedException.class,
                () -> carrierOfferService.saveDTO(dto)
        );

        verify(freightRequestService).requireCanAddTo(requestId);
        verifyNoInteractions(carrierOfferMapper);
        verify(carrierOfferRepository, never()).save(any());
    }
}
