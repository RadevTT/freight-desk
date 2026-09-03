package bg.freightdesk.repository;

import bg.freightdesk.entity.FreightRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FreightRequestRepository extends JpaRepository<FreightRequest, UUID> {
}