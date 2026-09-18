package bg.freightdesk.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "carrier_offers")
public class CarrierOffer {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "freight_request_id", nullable = false)
    private FreightRequest freightRequest;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne
    @JoinColumn(name = "carrier_id")
    private Carrier carrier;

    @NotNull(message = "Offered price is required")
    @Column(nullable = false)
    private BigDecimal offeredPrice;

    private String currency;

    private LocalDateTime offerDate;

    private String note;

    private boolean selected;

    public CarrierOffer() {
    }

    public UUID getId() {
        return id;
    }

    public FreightRequest getFreightRequest() {
        return freightRequest;
    }

    public void setFreightRequest(FreightRequest freightRequest) {
        this.freightRequest = freightRequest;
    }

    public Carrier getCarrier() {
        return carrier;
    }

    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }

    public BigDecimal getOfferedPrice() {
        return offeredPrice;
    }

    public void setOfferedPrice(BigDecimal offeredPrice) {
        this.offeredPrice = offeredPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDateTime getOfferDate() {
        return offerDate;
    }

    public void setOfferDate(LocalDateTime offerDate) {
        this.offerDate = offerDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}