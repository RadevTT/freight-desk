package bg.freightdesk.service;

import bg.freightdesk.dto.FreightRequestDTO;
import bg.freightdesk.entity.CarrierOffer;
import bg.freightdesk.entity.FreightDocument;
import bg.freightdesk.entity.FreightRequest;
import bg.freightdesk.entity.User;
import bg.freightdesk.mapper.FreightRequestMapper;
import bg.freightdesk.model.ColleagueAccessLevel;
import bg.freightdesk.model.RequestStatus;
import bg.freightdesk.repository.CarrierOfferRepository;
import bg.freightdesk.repository.FreightDocumentRepository;
import bg.freightdesk.repository.FreightRequestRepository;
import bg.freightdesk.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class FreightRequestService {

    private final FreightRequestRepository freightRequestRepository;
    private final FreightRequestMapper freightRequestMapper;
    private final CarrierOfferRepository carrierOfferRepository;
    private final UserRepository userRepository;
    private final FreightDocumentRepository freightDocumentRepository;

    public FreightRequestService(
            FreightRequestRepository freightRequestRepository,
            FreightRequestMapper freightRequestMapper,
            CarrierOfferRepository carrierOfferRepository,
            UserRepository userRepository,
            FreightDocumentRepository freightDocumentRepository) {

        this.freightRequestRepository = freightRequestRepository;
        this.freightRequestMapper = freightRequestMapper;
        this.carrierOfferRepository = carrierOfferRepository;
        this.userRepository = userRepository;
        this.freightDocumentRepository = freightDocumentRepository;
    }

    private User getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            throw new AccessDeniedException("Authentication required");
        }

        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() ->
                        new AccessDeniedException("Current user not found"));
    }

    private boolean isAdmin(User user) {
        return user.getRole() != null
                && "ADMIN".equals(user.getRole().name());
    }

    private boolean belongsToSameOrganization(
            User owner,
            User currentUser) {

        return owner != null
                && owner.getOrganization() != null
                && currentUser.getOrganization() != null
                && owner.getOrganization().getId()
                        .equals(currentUser.getOrganization().getId());
    }

    /**
     * Проверява дали потребителят може да вижда заявката.
     * Администраторът и собственикът винаги имат достъп
     * до заявките в собствената организация.
     */
    private boolean canView(
            FreightRequest request,
            User currentUser) {

        User owner = request.getForwarder();

        if (!belongsToSameOrganization(owner, currentUser)) {
            return false;
        }

        if (isAdmin(currentUser)
                || owner.getId().equals(currentUser.getId())) {
            return true;
        }

        return owner.isVisibleToColleagues();
    }

    /**
     * Проверява дали потребителят може да добавя или редактира
     * информация по заявката, включително оферти и статус.
     */
    private boolean canAddTo(
            FreightRequest request,
            User currentUser) {

        User owner = request.getForwarder();

        if (!belongsToSameOrganization(owner, currentUser)) {
            return false;
        }

        if (isAdmin(currentUser)
                || owner.getId().equals(currentUser.getId())) {
            return true;
        }

        ColleagueAccessLevel accessLevel =
                owner.getColleagueAccessLevel();

        return owner.isVisibleToColleagues()
                && (accessLevel == ColleagueAccessLevel.VIEW_AND_ADD
                || accessLevel == ColleagueAccessLevel.FULL_ACCESS);
    }

    /**
     * Проверява дали потребителят има пълен достъп,
     * включително право за изтриване.
     */
    private boolean canManage(
            FreightRequest request,
            User currentUser) {

        User owner = request.getForwarder();

        if (!belongsToSameOrganization(owner, currentUser)) {
            return false;
        }

        if (isAdmin(currentUser)
                || owner.getId().equals(currentUser.getId())) {
            return true;
        }

        return owner.isVisibleToColleagues()
                && owner.getColleagueAccessLevel()
                        == ColleagueAccessLevel.FULL_ACCESS;
    }

    private FreightRequest getAccessibleRequest(UUID id) {
        FreightRequest request = freightRequestRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Freight request not found"));

        User currentUser = getCurrentUser();

        if (!canView(request, currentUser)) {
            throw new AccessDeniedException(
                    "You do not have access to this freight request");
        }

        return request;
    }

    public void requireCanView(UUID requestId) {
        getAccessibleRequest(requestId);
    }

    public void requireCanAddTo(UUID requestId) {
        FreightRequest request = getAccessibleRequest(requestId);
        User currentUser = getCurrentUser();

        if (!canAddTo(request, currentUser)) {
            throw new AccessDeniedException(
                    "You do not have permission to add to this freight request");
        }
    }

    public void requireCanManage(UUID requestId) {
        FreightRequest request = getAccessibleRequest(requestId);
        User currentUser = getCurrentUser();

        if (!canManage(request, currentUser)) {
            throw new AccessDeniedException(
                    "You do not have permission to manage this freight request");
        }
    }

    @Transactional
    public void markOfferSentIfNoSelectedOffer(UUID requestId) {
        FreightRequest request = getAccessibleRequest(requestId);
        User currentUser = getCurrentUser();

        if (!canAddTo(request, currentUser)) {
            throw new AccessDeniedException(
                    "You do not have permission to add an offer");
        }

        CarrierOffer selectedOffer =
                carrierOfferRepository
                        .findByFreightRequestIdAndSelectedTrue(requestId);

        if (selectedOffer == null) {
            request.setStatus(RequestStatus.OFFER_SENT);
            freightRequestRepository.save(request);
        }
    }

    @Transactional(readOnly = true)
    public List<FreightRequestDTO> findAll() {
        User currentUser = getCurrentUser();

        return freightRequestRepository.findAll()
                .stream()
                .filter(request -> canView(request, currentUser))
                .map(freightRequestMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public FreightRequestDTO findDTOById(UUID id) {
        FreightRequest request = getAccessibleRequest(id);
        return freightRequestMapper.toDTO(request);
    }

    @Transactional
    public void saveDTO(FreightRequestDTO dto) {
        User currentUser = getCurrentUser();

        // Нова заявка: собственикът винаги е влезлият потребител.
        if (dto.getId() == null) {
            FreightRequest request = freightRequestMapper.toEntity(dto);

            request.setForwarder(currentUser);

            if (request.getStatus() == null) {
                request.setStatus(RequestStatus.NEW);
            }

            calculateProfit(request);
            freightRequestRepository.save(request);
            return;
        }

        // Редакция на съществуваща заявка.
        FreightRequest existingRequest =
                getAccessibleRequest(dto.getId());

        // VIEW_AND_ADD също може да редактира.
        if (!canAddTo(existingRequest, currentUser)) {
            throw new AccessDeniedException(
                    "You do not have permission to edit this freight request");
        }

        FreightRequest updatedRequest =
                freightRequestMapper.toEntity(dto);

        // Собственикът не може да се сменя през формата.
        updatedRequest.setForwarder(existingRequest.getForwarder());

        // Запазваме избраната оферта като източник на цената
        // за превозвача.
        CarrierOffer selectedOffer =
                carrierOfferRepository
                        .findByFreightRequestIdAndSelectedTrue(dto.getId());

        if (selectedOffer != null) {
            updatedRequest.setCarrier(selectedOffer.getCarrier());
            updatedRequest.setCarrierPrice(
                    selectedOffer.getOfferedPrice());
        }

        // Ако формата не изпрати статус, запазваме стария.
        if (dto.getStatus() == null
                || dto.getStatus().isBlank()) {
            updatedRequest.setStatus(existingRequest.getStatus());
        }

        calculateProfit(updatedRequest);
        freightRequestRepository.save(updatedRequest);
    }

    private void calculateProfit(FreightRequest request) {
        if (request.getClientPrice() != null
                && request.getCarrierPrice() != null) {
            request.setProfit(
                    request.getClientPrice()
                            .subtract(request.getCarrierPrice()));
        } else {
            request.setProfit(null);
        }
    }

    @Transactional
    public void delete(UUID id) {
        FreightRequest request = getAccessibleRequest(id);
        User currentUser = getCurrentUser();

        // Изтриване е разрешено само на собственика,
        // администратора или колега с FULL_ACCESS.
        if (!canManage(request, currentUser)) {
            throw new AccessDeniedException(
                    "You do not have permission to delete this freight request");
        }

        List<FreightDocument> documents =
                freightDocumentRepository.findByFreightRequestId(id);

        // Изтриваме физическите файлове.
        for (FreightDocument document : documents) {
            String filePath = document.getFilePath();

            if (filePath != null && !filePath.isBlank()) {
                try {
                    Files.deleteIfExists(Paths.get(filePath));
                } catch (IOException e) {
                    throw new RuntimeException(
                            "Could not delete document file: " + filePath,
                            e);
                }
            }
        }

        // Изтриваме документите и офертите преди заявката.
        freightDocumentRepository.deleteAll(documents);
        carrierOfferRepository.deleteByFreightRequestId(id);

        freightRequestRepository.delete(request);
    }

    @Transactional
    public void selectCarrierOffer(UUID offerId) {
        CarrierOffer offer = carrierOfferRepository.findById(offerId)
                .orElseThrow(() ->
                        new RuntimeException("Carrier offer not found"));

        UUID requestId = offer.getFreightRequest().getId();
        FreightRequest request = getAccessibleRequest(requestId);
        User currentUser = getCurrentUser();

        // VIEW_AND_ADD има право да избира оферта.
        if (!canAddTo(request, currentUser)) {
            throw new AccessDeniedException(
                    "You do not have permission to select a carrier offer");
        }

        request.setCarrier(offer.getCarrier());
        request.setCarrierPrice(offer.getOfferedPrice());

        calculateProfit(request);
        request.setStatus(RequestStatus.CONFIRMED);

        freightRequestRepository.save(request);
    }

    @Transactional
    public void updateStatus(UUID id, RequestStatus status) {
        FreightRequest request = getAccessibleRequest(id);
        User currentUser = getCurrentUser();

        // VIEW_AND_ADD има право да променя статуса.
        if (!canAddTo(request, currentUser)) {
            throw new AccessDeniedException(
                    "You do not have permission to change the request status");
        }

        request.setStatus(status);
        freightRequestRepository.save(request);
    }

    @Transactional
    public void deleteCarrierOffer(UUID offerId) {
        CarrierOffer offer = carrierOfferRepository.findById(offerId)
                .orElseThrow(() ->
                        new RuntimeException("Carrier offer not found"));

        UUID requestId = offer.getFreightRequest().getId();
        FreightRequest request = getAccessibleRequest(requestId);
        User currentUser = getCurrentUser();

        // Изтриване на оферта изисква пълен достъп.
        if (!canManage(request, currentUser)) {
            throw new AccessDeniedException(
                    "You do not have permission to delete a carrier offer");
        }

        if (offer.isSelected()) {
            request.setCarrier(null);
            request.setCarrierPrice(null);
            request.setProfit(null);
            request.setStatus(RequestStatus.OFFER_SENT);

            freightRequestRepository.save(request);
        }

        carrierOfferRepository.delete(offer);
    }
}