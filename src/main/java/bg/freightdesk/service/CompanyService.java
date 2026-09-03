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

        return companyRepository.findAll()
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

    public void saveDTO(CompanyDTO dto) {

        Company company =
                companyMapper.toEntity(dto);

        companyRepository.save(company);
    }

    public void delete(UUID id) {
        companyRepository.deleteById(id);
    }

    public Company findEntityById(UUID id) {

        return companyRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Company not found"));
    }

    public List<CompanyDTO> findAllByType(CompanyType type) {

        return companyRepository.findAllByType(type)
                .stream()
                .map(companyMapper::toDTO)
                .toList();
    }
}