package bg.freightdesk.repository;

import bg.freightdesk.entity.Carrier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CarrierRepository extends JpaRepository<Carrier, UUID> {

    List<Carrier> findAllByActiveTrue();

    List<Carrier> findAllByActiveFalse();

    List<Carrier> findAllByCompany_IdAndActiveTrue(UUID companyId);

    List<Carrier> findAllByCompany_IdAndActiveFalse(UUID companyId);
}