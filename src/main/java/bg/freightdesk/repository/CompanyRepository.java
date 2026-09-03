package bg.freightdesk.repository;

import bg.freightdesk.entity.Company;
import bg.freightdesk.model.CompanyType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {

    List<Company> findAllByType(CompanyType type);
}