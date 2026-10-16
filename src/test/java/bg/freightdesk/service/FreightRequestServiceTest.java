package bg.freightdesk.service;

import bg.freightdesk.dto.FreightRequestDTO;
import bg.freightdesk.entity.CarrierOffer;
import bg.freightdesk.entity.FreightRequest;
import bg.freightdesk.entity.Organization;
import bg.freightdesk.entity.User;
import bg.freightdesk.mapper.FreightRequestMapper;
import bg.freightdesk.model.ColleagueAccessLevel;
import bg.freightdesk.model.RequestStatus;
import bg.freightdesk.repository.CarrierOfferRepository;
import bg.freightdesk.repository.FreightDocumentRepository;
import bg.freightdesk.repository.FreightRequestRepository;
import bg.freightdesk.repository.UserRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FreightRequestServiceTest {

    private FreightRequestRepository freightRequestRepository;
    private FreightRequestMapper freightRequestMapper;
    private CarrierOfferRepository carrierOfferRepository;
    private UserRepository userRepository;
    private FreightDocumentRepository freightDocumentRepository;

    private FreightRequestService freightRequestService;

    @BeforeEach
    void setUp() {
        freightRequestRepository = mock(FreightRequestRepository.class);
        freightRequestMapper = mock(FreightRequestMapper.class);
        carrierOfferRepository = mock(CarrierOfferRepository.class);
        userRepository = mock(UserRepository.class);
        freightDocumentRepository = mock(FreightDocumentRepository.class);

        freightRequestService = new FreightRequestService(
                freightRequestRepository,
                freightRequestMapper,
                carrierOfferRepository,
                userRepository,
                freightDocumentRepository
        );
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void colleagueWithViewOnlyCannotDeleteAnotherUsersRequest() {
        UUID organizationId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();

        Organization organization = new Organization();
        organization.setId(organizationId);

        User owner = new User();
        owner.setId(UUID.randomUUID());
        owner.setOrganization(organization);
        owner.setVisibleToColleagues(true);
        owner.setColleagueAccessLevel(ColleagueAccessLevel.VIEW_ONLY);

        User colleague = new User();
        colleague.setId(UUID.randomUUID());
        colleague.setOrganization(organization);

        FreightRequest request = new FreightRequest();
        request.setId(requestId);
        request.setForwarder(owner);

        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated(
                        "colleague",
                        "password",
                        Collections.emptyList()
                )
        );

        when(userRepository.findByUsername("colleague"))
                .thenReturn(Optional.of(colleague));

        when(freightRequestRepository.findById(requestId))
                .thenReturn(Optional.of(request));

        assertThrows(
                AccessDeniedException.class,
                () -> freightRequestService.delete(requestId)
        );

        verify(carrierOfferRepository, never())
                .deleteByFreightRequestId(any());

        verify(freightDocumentRepository, never())
                .findByFreightRequestId(any());

        verify(freightRequestRepository, never())
                .delete(any(FreightRequest.class));
    }

    @Test
    void userFromAnotherOrganizationCannotViewRequest() {
        UUID requestId = UUID.randomUUID();

        Organization ownerOrganization = new Organization();
        ownerOrganization.setId(UUID.randomUUID());

        Organization otherOrganization = new Organization();
        otherOrganization.setId(UUID.randomUUID());

        User owner = new User();
        owner.setId(UUID.randomUUID());
        owner.setOrganization(ownerOrganization);
        owner.setVisibleToColleagues(true);
        owner.setColleagueAccessLevel(ColleagueAccessLevel.FULL_ACCESS);

        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());
        otherUser.setOrganization(otherOrganization);

        FreightRequest request = new FreightRequest();
        request.setId(requestId);
        request.setForwarder(owner);

        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated(
                        "other-user",
                        "password",
                        Collections.emptyList()
                )
        );

        when(userRepository.findByUsername("other-user"))
                .thenReturn(Optional.of(otherUser));

        when(freightRequestRepository.findById(requestId))
                .thenReturn(Optional.of(request));

        assertThrows(
                AccessDeniedException.class,
                () -> freightRequestService.requireCanView(requestId)
        );

        verifyNoInteractions(carrierOfferRepository);
        verifyNoInteractions(freightDocumentRepository);
    }

    @Test
    void colleagueWithViewAndAddCanMarkRequestAsOfferSent() {
        UUID organizationId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();

        Organization organization = new Organization();
        organization.setId(organizationId);

        User owner = new User();
        owner.setId(UUID.randomUUID());
        owner.setOrganization(organization);
        owner.setVisibleToColleagues(true);
        owner.setColleagueAccessLevel(ColleagueAccessLevel.VIEW_AND_ADD);

        User colleague = new User();
        colleague.setId(UUID.randomUUID());
        colleague.setOrganization(organization);

        FreightRequest request = new FreightRequest();
        request.setId(requestId);
        request.setForwarder(owner);

        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated(
                        "colleague",
                        "password",
                        Collections.emptyList()
                )
        );

        when(userRepository.findByUsername("colleague"))
                .thenReturn(Optional.of(colleague));

        when(freightRequestRepository.findById(requestId))
                .thenReturn(Optional.of(request));

        when(carrierOfferRepository.findByFreightRequestIdAndSelectedTrue(requestId))
                .thenReturn(null);

        freightRequestService.markOfferSentIfNoSelectedOffer(requestId);

        assertEquals(RequestStatus.OFFER_SENT, request.getStatus());

        verify(freightRequestRepository).save(request);
    }

    @Test
    void colleagueWithViewAndAddCannotEditRequest() {
        UUID organizationId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();

        Organization organization = new Organization();
        organization.setId(organizationId);

        User owner = new User();
        owner.setId(UUID.randomUUID());
        owner.setOrganization(organization);
        owner.setVisibleToColleagues(true);
        owner.setColleagueAccessLevel(ColleagueAccessLevel.VIEW_AND_ADD);

        User colleague = new User();
        colleague.setId(UUID.randomUUID());
        colleague.setOrganization(organization);

        FreightRequest request = new FreightRequest();
        request.setId(requestId);
        request.setForwarder(owner);

        FreightRequestDTO dto = new FreightRequestDTO();
        dto.setId(requestId);

        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated(
                        "colleague",
                        "password",
                        Collections.emptyList()
                )
        );

        when(userRepository.findByUsername("colleague"))
                .thenReturn(Optional.of(colleague));

        when(freightRequestRepository.findById(requestId))
                .thenReturn(Optional.of(request));

        assertThrows(
                AccessDeniedException.class,
                () -> freightRequestService.saveDTO(dto)
        );

        verify(freightRequestRepository, never())
                .save(any(FreightRequest.class));
    }

    @Test
    void colleagueWithViewOnlyCannotAddToRequest() {
        UUID organizationId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();

        Organization organization = new Organization();
        organization.setId(organizationId);

        User owner = new User();
        owner.setId(UUID.randomUUID());
        owner.setOrganization(organization);
        owner.setVisibleToColleagues(true);
        owner.setColleagueAccessLevel(ColleagueAccessLevel.VIEW_ONLY);

        User colleague = new User();
        colleague.setId(UUID.randomUUID());
        colleague.setOrganization(organization);

        FreightRequest request = new FreightRequest();
        request.setId(requestId);
        request.setForwarder(owner);

        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated(
                        "colleague",
                        "password",
                        Collections.emptyList()
                )
        );

        when(userRepository.findByUsername("colleague"))
                .thenReturn(Optional.of(colleague));

        when(freightRequestRepository.findById(requestId))
                .thenReturn(Optional.of(request));

        assertThrows(
                AccessDeniedException.class,
                () -> freightRequestService.requireCanAddTo(requestId)
        );
    }

    @Test
    void colleagueWithViewAndAddCannotManageRequest() {
        UUID organizationId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();

        Organization organization = new Organization();
        organization.setId(organizationId);

        User owner = new User();
        owner.setId(UUID.randomUUID());
        owner.setOrganization(organization);
        owner.setVisibleToColleagues(true);
        owner.setColleagueAccessLevel(ColleagueAccessLevel.VIEW_AND_ADD);

        User colleague = new User();
        colleague.setId(UUID.randomUUID());
        colleague.setOrganization(organization);

        FreightRequest request = new FreightRequest();
        request.setId(requestId);
        request.setForwarder(owner);

        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated(
                        "colleague",
                        "password",
                        Collections.emptyList()
                )
        );

        when(userRepository.findByUsername("colleague"))
                .thenReturn(Optional.of(colleague));

        when(freightRequestRepository.findById(requestId))
                .thenReturn(Optional.of(request));

        assertThrows(
                AccessDeniedException.class,
                () -> freightRequestService.requireCanManage(requestId)
        );
    }

    @Test
    void colleagueWithFullAccessCanManageRequest() {
        UUID organizationId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();

        Organization organization = new Organization();
        organization.setId(organizationId);

        User owner = new User();
        owner.setId(UUID.randomUUID());
        owner.setOrganization(organization);
        owner.setVisibleToColleagues(true);
        owner.setColleagueAccessLevel(ColleagueAccessLevel.FULL_ACCESS);

        User colleague = new User();
        colleague.setId(UUID.randomUUID());
        colleague.setOrganization(organization);

        FreightRequest request = new FreightRequest();
        request.setId(requestId);
        request.setForwarder(owner);

        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated(
                        "colleague",
                        "password",
                        Collections.emptyList()
                )
        );

        when(userRepository.findByUsername("colleague"))
                .thenReturn(Optional.of(colleague));

        when(freightRequestRepository.findById(requestId))
                .thenReturn(Optional.of(request));

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(
                () -> freightRequestService.requireCanManage(requestId)
        );
    }

    @Test
    void colleagueWithViewAndAddCannotSelectCarrierOffer() {
        UUID organizationId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        UUID offerId = UUID.randomUUID();

        Organization organization = new Organization();
        organization.setId(organizationId);

        User owner = new User();
        owner.setId(UUID.randomUUID());
        owner.setOrganization(organization);
        owner.setVisibleToColleagues(true);
        owner.setColleagueAccessLevel(ColleagueAccessLevel.VIEW_AND_ADD);

        User colleague = new User();
        colleague.setId(UUID.randomUUID());
        colleague.setOrganization(organization);

        FreightRequest request = new FreightRequest();
        request.setId(requestId);
        request.setForwarder(owner);

        CarrierOffer offer = mock(CarrierOffer.class);
        when(offer.getId()).thenReturn(offerId);
        when(offer.getFreightRequest()).thenReturn(request);

        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated(
                        "colleague",
                        "password",
                        Collections.emptyList()
                )
        );

        when(userRepository.findByUsername("colleague"))
                .thenReturn(Optional.of(colleague));

        when(carrierOfferRepository.findById(offerId))
                .thenReturn(Optional.of(offer));

        when(freightRequestRepository.findById(requestId))
                .thenReturn(Optional.of(request));

        assertThrows(
                AccessDeniedException.class,
                () -> freightRequestService.selectCarrierOffer(offerId)
        );

        verify(freightRequestRepository, never()).save(any());
    }

    @Test
    void colleagueWithViewAndAddCannotDeleteCarrierOffer() {
        UUID organizationId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        UUID offerId = UUID.randomUUID();

        Organization organization = new Organization();
        organization.setId(organizationId);

        User owner = new User();
        owner.setId(UUID.randomUUID());
        owner.setOrganization(organization);
        owner.setVisibleToColleagues(true);
        owner.setColleagueAccessLevel(ColleagueAccessLevel.VIEW_AND_ADD);

        User colleague = new User();
        colleague.setId(UUID.randomUUID());
        colleague.setOrganization(organization);

        FreightRequest request = new FreightRequest();
        request.setId(requestId);
        request.setForwarder(owner);

        CarrierOffer offer = mock(CarrierOffer.class);
        when(offer.getId()).thenReturn(offerId);
        when(offer.getFreightRequest()).thenReturn(request);

        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated(
                        "colleague",
                        "password",
                        Collections.emptyList()
                )
        );

        when(userRepository.findByUsername("colleague"))
                .thenReturn(Optional.of(colleague));

        when(carrierOfferRepository.findById(offerId))
                .thenReturn(Optional.of(offer));

        when(freightRequestRepository.findById(requestId))
                .thenReturn(Optional.of(request));

        assertThrows(
                AccessDeniedException.class,
                () -> freightRequestService.deleteCarrierOffer(offerId)
        );

        verify(carrierOfferRepository, never())
                .delete(any(CarrierOffer.class));

        verify(freightRequestRepository, never()).save(any());
    }

    @Test
    void colleagueWithViewAndAddCannotChangeRequestStatus() {
        UUID organizationId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();

        Organization organization = new Organization();
        organization.setId(organizationId);

        User owner = new User();
        owner.setId(UUID.randomUUID());
        owner.setOrganization(organization);
        owner.setVisibleToColleagues(true);
        owner.setColleagueAccessLevel(ColleagueAccessLevel.VIEW_AND_ADD);

        User colleague = new User();
        colleague.setId(UUID.randomUUID());
        colleague.setOrganization(organization);

        FreightRequest request = new FreightRequest();
        request.setId(requestId);
        request.setForwarder(owner);
        request.setStatus(RequestStatus.NEW);

        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated(
                        "colleague",
                        "password",
                        Collections.emptyList()
                )
        );

        when(userRepository.findByUsername("colleague"))
                .thenReturn(Optional.of(colleague));

        when(freightRequestRepository.findById(requestId))
                .thenReturn(Optional.of(request));

        assertThrows(
                AccessDeniedException.class,
                () -> freightRequestService.updateStatus(
                        requestId,
                        RequestStatus.CONFIRMED
                )
        );

        assertEquals(RequestStatus.NEW, request.getStatus());

        verify(freightRequestRepository, never()).save(any());
    }
}