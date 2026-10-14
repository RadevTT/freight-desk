package bg.freightdesk.controller;

import bg.freightdesk.entity.FreightDocument;
import bg.freightdesk.model.DocumentType;
import bg.freightdesk.service.FreightDocumentService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/freight-requests/{freightRequestId}/documents")
public class FreightDocumentController {

        private final FreightDocumentService freightDocumentService;

        public FreightDocumentController(FreightDocumentService freightDocumentService) {
                this.freightDocumentService = freightDocumentService;
        }

        @PostMapping("/upload")
        public String uploadDocument(
                        @PathVariable UUID freightRequestId,
                        @RequestParam("file") MultipartFile file,
                        @RequestParam("documentType") DocumentType documentType,
                        @RequestParam(value = "invoiceNumber", required = false) String invoiceNumber)
                        throws IOException {

                freightDocumentService.uploadDocument(
                                freightRequestId,
                                file,
                                documentType,
                                invoiceNumber);

                return "redirect:/freight-requests";
        }

        @GetMapping
        @ResponseBody
        public List<FreightDocument> getDocuments(
                        @PathVariable UUID freightRequestId) {
                return freightDocumentService.getDocuments(freightRequestId);
        }

        @GetMapping("/{documentId}/view")
        public ResponseEntity<Resource> viewDocument(
                        @PathVariable UUID freightRequestId,
                        @PathVariable UUID documentId) throws IOException {

                FreightDocument document = freightDocumentService.getDocument(documentId);

                if (!document.getFreightRequest().getId().equals(freightRequestId)) {
                        return ResponseEntity.notFound().build();
                }

                Path path = Paths.get(document.getFilePath());

                if (!Files.exists(path)) {
                        return ResponseEntity.notFound().build();
                }

                Resource resource = new UrlResource(path.toUri());

                String contentType = Files.probeContentType(path);

                if (contentType == null) {
                        contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
                }

                ContentDisposition contentDisposition = ContentDisposition.inline()
                                .filename(
                                                document.getFileName(),
                                                StandardCharsets.UTF_8)
                                .build();

                return ResponseEntity.ok()
                                .contentType(MediaType.parseMediaType(contentType))
                                .header(
                                                HttpHeaders.CONTENT_DISPOSITION,
                                                contentDisposition.toString())
                                .body(resource);
        }

        @GetMapping("/{documentId}/download")
        public ResponseEntity<Resource> downloadDocument(
                        @PathVariable UUID freightRequestId,
                        @PathVariable UUID documentId) throws IOException {

                FreightDocument document = freightDocumentService.getDocument(documentId);

                if (!document.getFreightRequest().getId().equals(freightRequestId)) {
                        return ResponseEntity.notFound().build();
                }
                Path path = Paths.get(document.getFilePath());

                if (!Files.exists(path)) {
                        return ResponseEntity.notFound().build();
                }

                Resource resource = new UrlResource(path.toUri());

                ContentDisposition contentDisposition = ContentDisposition.attachment()
                                .filename(
                                                document.getFileName(),
                                                StandardCharsets.UTF_8)
                                .build();

                return ResponseEntity.ok()
                                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                .header(
                                                HttpHeaders.CONTENT_DISPOSITION,
                                                contentDisposition.toString())
                                .body(resource);
        }

        @PostMapping("/{documentId}/delete")
        public String deleteDocument(
                        @PathVariable UUID freightRequestId,
                        @PathVariable UUID documentId) {

                FreightDocument document = freightDocumentService.getDocument(documentId);

                if (!document.getFreightRequest().getId().equals(freightRequestId)) {
                        throw new IllegalArgumentException("Document does not belong to this freight request.");
                }

                freightDocumentService.deleteDocument(documentId);

                return "redirect:/freight-requests";
        }
}