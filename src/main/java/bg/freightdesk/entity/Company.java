package bg.freightdesk.entity;

import bg.freightdesk.model.CompanyType;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Company {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    private String companyCode;

    @Enumerated(EnumType.STRING)
    private CompanyType type;

    public Company() {
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