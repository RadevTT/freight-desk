
package bg.freightdesk.service;

import bg.freightdesk.entity.FreightDocument;
import bg.freightdesk.entity.FreightRequest;
import bg.freightdesk.repository.FreightDocumentRepository;
import bg.freightdesk.repository.FreightRequestRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FreightDocumentServiceTest {

    private FreightDocumentRepository freightDocumentRepository;
    private FreightRequestRepository freightRequestRepository;
    private FreightRequestService freightRequestService;

    private FreightDocumentService freightDocumentService;

    @BeforeEach
    void setUp() {
        freightDocumentRepository = mock(FreightDocumentRepository.class);
        freightRequestRepository = mock(FreightRequestRepository.class);
        freightRequestService = mock(FreightRequestService.class);

        freightDocumentService = new FreightDocumentService(
                freightDocumentRepository,
                freightRequestRepository,
                freightRequestService
        );
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void cannotUploadDocumentWithoutAddPermission() throws Exception {
        UUID requestId = UUID.randomUUID();

        MultipartFile file = mock(MultipartFile.class);

        doThrow(new AccessDeniedException("Access denied"))
                .when(freightRequestService)
                .requireCanAddTo(requestId);

        assertThrows(
                AccessDeniedException.class,
                () -> freightDocumentService.uploadDocument(
                        requestId,
                        file,
                        null,
                        null
                )
        );

        verify(freightRequestService).requireCanAddTo(requestId);

        verifyNoInteractions(freightRequestRepository);
        verifyNoInteractions(freightDocumentRepository);
        verify(file, never()).getInputStream();
    }

    @Test
    void cannotViewDocumentsWithoutViewPermission() {
        UUID requestId = UUID.randomUUID();

        doThrow(new AccessDeniedException("Access denied"))
                .when(freightRequestService)
                .requireCanView(requestId);

        assertThrows(
                AccessDeniedException.class,
                () -> freightDocumentService.getDocuments(requestId)
        );

        verify(freightRequestService).requireCanView(requestId);
        verifyNoInteractions(freightDocumentRepository);
    }

    @Test
    void cannotViewSingleDocumentWithoutViewPermission() {
        UUID requestId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();

        FreightRequest request = new FreightRequest();
        request.setId(requestId);

        FreightDocument document = new FreightDocument();
        document.setFreightRequest(request);

        when(freightDocumentRepository.findById(documentId))
                .thenReturn(Optional.of(document));

        doThrow(new AccessDeniedException("Access denied"))
                .when(freightRequestService)
                .requireCanView(requestId);

        assertThrows(
                AccessDeniedException.class,
                () -> freightDocumentService.getDocument(documentId)
        );

        verify(freightRequestService).requireCanView(requestId);
    }

    @Test
    void cannotDeleteDocumentWithoutManagePermission() {
        UUID requestId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();

        FreightRequest request = new FreightRequest();
        request.setId(requestId);

        FreightDocument document = new FreightDocument();
        document.setFreightRequest(request);

        when(freightDocumentRepository.findById(documentId))
                .thenReturn(Optional.of(document));

        doNothing()
                .when(freightRequestService)
                .requireCanView(requestId);

        doThrow(new AccessDeniedException("Access denied"))
                .when(freightRequestService)
                .requireCanManage(requestId);

        assertThrows(
                AccessDeniedException.class,
                () -> freightDocumentService.deleteDocument(documentId)
        );

        verify(freightRequestService).requireCanView(requestId);
        verify(freightRequestService).requireCanManage(requestId);

        verify(freightDocumentRepository, never())
                .delete(any(FreightDocument.class));
    }
}
