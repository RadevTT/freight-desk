package bg.freightdesk.service;

import bg.freightdesk.entity.FreightDocument;
import bg.freightdesk.entity.FreightRequest;
import bg.freightdesk.model.DocumentType;
import bg.freightdesk.repository.FreightDocumentRepository;
import bg.freightdesk.repository.FreightRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FreightDocumentService {

    private final FreightDocumentRepository freightDocumentRepository;
    private final FreightRequestRepository freightRequestRepository;
    private final FreightRequestService freightRequestService;

    private final Path uploadDirectory = Paths.get("uploads", "freight");

    public FreightDocumentService(
            FreightDocumentRepository freightDocumentRepository,
            FreightRequestRepository freightRequestRepository,
            FreightRequestService freightRequestService) {
        this.freightDocumentRepository = freightDocumentRepository;
        this.freightRequestRepository = freightRequestRepository;
        this.freightRequestService = freightRequestService;
    }

    @Transactional
    public FreightDocument uploadDocument(
            UUID freightRequestId,
            MultipartFile file,
            DocumentType documentType,
            String invoiceNumber) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty.");
        }

        freightRequestService.requireCanAddTo(freightRequestId);
        FreightRequest freightRequest = freightRequestRepository.findById(freightRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Freight request not found."));

        Path requestDirectory = uploadDirectory.resolve(freightRequestId.toString());

        Files.createDirectories(requestDirectory);

        /*
         * For the three main document types we keep only one document.
         * OTHER documents can be multiple.
         */
       if (documentType != DocumentType.OTHER) {
    List<FreightDocument> existingDocuments =
            freightDocumentRepository.findByFreightRequestAndDocumentType(
                    freightRequest,
                    documentType);

    if (!existingDocuments.isEmpty()) {
        // Замяната на документ също изисква право за управление.
        freightRequestService.requireCanManage(freightRequestId);

        for (FreightDocument existingDocument : existingDocuments) {
            deletePhysicalFile(existingDocument);
            freightDocumentRepository.delete(existingDocument);
        }
    }
}

        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null || originalFileName.isBlank()) {
            originalFileName = "document";
        }

        /*
         * We do NOT use the original filename as the physical filename.
         * A UUID prevents filename collisions and unsafe paths.
         */
        String storedFileName = UUID.randomUUID() + "_" + sanitizeFileName(originalFileName);

        Path targetPath = requestDirectory.resolve(storedFileName);

        Files.copy(
                file.getInputStream(),
                targetPath,
                StandardCopyOption.REPLACE_EXISTING);

        FreightDocument document = new FreightDocument();

        document.setFreightRequest(freightRequest);
        document.setDocumentType(documentType);
        document.setFileName(originalFileName);
        document.setFilePath(targetPath.toString());
        document.setInvoiceNumber(invoiceNumber);
        document.setUploadedAt(LocalDateTime.now());

        return freightDocumentRepository.save(document);
    }

    @Transactional(readOnly = true)
    public List<FreightDocument> getDocuments(UUID freightRequestId) {
        freightRequestService.requireCanView(freightRequestId);

        return freightDocumentRepository.findByFreightRequestId(freightRequestId);
    }

    @Transactional(readOnly = true)
    public FreightDocument getDocument(UUID documentId) {
        FreightDocument document = freightDocumentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found."));

        freightRequestService.requireCanView(
                document.getFreightRequest().getId());

        return document;
    }

    @Transactional
    public void deleteDocument(UUID documentId) {
        FreightDocument document = getDocument(documentId);

        freightRequestService.requireCanManage(
                document.getFreightRequest().getId());

        deletePhysicalFile(document);
        freightDocumentRepository.delete(document);
    }

    private void deletePhysicalFile(FreightDocument document) {

        if (document.getFilePath() == null) {
            return;
        }

        try {
            Files.deleteIfExists(Paths.get(document.getFilePath()));
        } catch (IOException e) {
            throw new RuntimeException("Could not delete document file.", e);
        }
    }

    private String sanitizeFileName(String fileName) {

        return fileName
                .replace("\\", "_")
                .replace("/", "_")
                .replace("..", "_");
    }
}