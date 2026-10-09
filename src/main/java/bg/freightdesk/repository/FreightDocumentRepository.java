package bg.freightdesk.repository;

import bg.freightdesk.entity.FreightDocument;
import bg.freightdesk.entity.FreightRequest;
import bg.freightdesk.model.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FreightDocumentRepository extends JpaRepository<FreightDocument, UUID> {

    List<FreightDocument> findByFreightRequestId(UUID freightRequestId);

    List<FreightDocument> findByFreightRequestAndDocumentType(
            FreightRequest freightRequest,
            DocumentType documentType
    );
}