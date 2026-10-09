package bg.freightdesk.entity;

import bg.freightdesk.model.DocumentType;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "freight_documents")
public class FreightDocument {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    private FreightRequest freightRequest;

    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    private String fileName;

    private String filePath;

    private String invoiceNumber;

    private LocalDateTime uploadedAt;

    public FreightDocument() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public FreightRequest getFreightRequest() {
        return freightRequest;
    }

    public void setFreightRequest(FreightRequest freightRequest) {
        this.freightRequest = freightRequest;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}