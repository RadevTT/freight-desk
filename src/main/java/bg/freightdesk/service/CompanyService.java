package bg.freightdesk.service;

import bg.freightdesk.dto.CompanyDTO;
import bg.freightdesk.entity.Company;
import bg.freightdesk.mapper.CompanyMapper;
import bg.freightdesk.model.CompanyType;
import bg.freightdesk.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    public CompanyService(
            CompanyRepository companyRepository,
            CompanyMapper companyMapper) {

        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
    }

    public List<CompanyDTO> findAll() {

        return companyRepository.findAllByActiveTrue()
                .stream()
                .map(companyMapper::toDTO)
                .toList();
    }

    public List<CompanyDTO> findInactive() {

        return companyRepository.findAllByActiveFalse()
                .stream()
                .map(companyMapper::toDTO)
                .toList();
    }

    public CompanyDTO findDTOById(UUID id) {

        Company company = companyRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Company not found"));

        return companyMapper.toDTO(company);
    }

    public Company findEntityById(UUID id) {

        return companyRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Company not found"));
    }

    public CompanyDTO saveDTO(CompanyDTO dto) {

        Company company =
                companyMapper.toEntity(dto);

        Company savedCompany =
                companyRepository.save(company);

        return companyMapper.toDTO(savedCompany);
    }

    public void deactivate(UUID id) {

        Company company =
                companyRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Company not found"));

        company.setActive(false);

        companyRepository.save(company);
    }

    public void activate(UUID id) {

        Company company =
                companyRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Company not found"));

        company.setActive(true);

        companyRepository.save(company);
    }

    public List<CompanyDTO> findAllByType(CompanyType type) {

        return companyRepository.findAllByType(type)
                .stream()
                .filter(Company::isActive)
                .map(companyMapper::toDTO)
                .toList();
    }

    public void delete(UUID id) {

        companyRepository.deleteById(id);
    }
}