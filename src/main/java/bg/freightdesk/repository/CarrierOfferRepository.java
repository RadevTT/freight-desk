package bg.freightdesk.repository;

import bg.freightdesk.entity.CarrierOffer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CarrierOfferRepository extends JpaRepository<CarrierOffer, UUID> {

    List<CarrierOffer> findByFreightRequestId(UUID freightRequestId);

    void deleteByFreightRequestId(UUID freightRequestId);
}