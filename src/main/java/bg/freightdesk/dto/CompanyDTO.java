package bg.freightdesk.dto;

import bg.freightdesk.model.CompanyType;

import java.util.UUID;

public class CompanyDTO {

    private UUID id;

    private String name;

    private String companyCode;

    private CompanyType type;

    public CompanyDTO() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public CompanyType getType() {
        return type;
    }

    public void setType(CompanyType type) {
        this.type = type;
    }
}