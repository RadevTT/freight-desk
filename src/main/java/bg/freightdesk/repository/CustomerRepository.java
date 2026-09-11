package bg.freightdesk.repository;

import bg.freightdesk.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    List<Customer> findAllByActiveTrue();

    List<Customer> findAllByActiveFalse();

    List<Customer> findAllByCompany_IdAndActiveTrue(UUID companyId);

    List<Customer> findAllByCompany_IdAndActiveFalse(UUID companyId);
}