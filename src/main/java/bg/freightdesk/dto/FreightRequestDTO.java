package bg.freightdesk.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class FreightRequestDTO {

    private UUID id;

    private LocalDate requestDate;

    private LocalDate loadingDate;

    private String deliveryTime;

    private String loadingCountry;

    private String loadingCity;

    private String loadingPostalCode;

    private String unloadingCountry;

    private String unloadingCity;

    private String unloadingPostalCode;

    private BigDecimal weight;

    private BigDecimal loadingMeters;

    private BigDecimal volume;

    private String dimensions;

    private Integer pallets;

    private String cargoType;

    private BigDecimal clientPrice;

    private BigDecimal carrierPrice;

    private String speditorPrice;

    private String additionalInfo;

    private BigDecimal profit;

    private UUID customerId;

    private UUID carrierId;

    private String status;

    private boolean newOfferAvailable;

    private boolean trailer;
    private boolean hanger;
    private boolean refrigerated;
    private boolean dualTemperature;
    private boolean van;
    private boolean microbus;
    private boolean clothingTruck;
    private boolean solo;
    private boolean jumbo;
    private boolean tractor;
    private boolean platform;
    private boolean flatbed;
    private boolean containerTruck;
    private boolean container;
    private boolean tanker;
    private boolean megatrailer;
    private boolean carCarrier;
    private boolean gondola;
    private boolean oversized;
    private boolean trailerOnly;
    private boolean tunnel;
    private boolean tailLift;
    private boolean crane;
    private boolean twoDrivers;
    private boolean palletTruck;

    public FreightRequestDTO() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }

    public LocalDate getLoadingDate() {
        return loadingDate;
    }

    public void setLoadingDate(LocalDate loadingDate) {
        this.loadingDate = loadingDate;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getLoadingCountry() {
        return loadingCountry;
    }

    public void setLoadingCountry(String loadingCountry) {
        this.loadingCountry = loadingCountry;
    }

    public String getLoadingCity() {
        return loadingCity;
    }

    public void setLoadingCity(String loadingCity) {
        this.loadingCity = loadingCity;
    }

    public String getLoadingPostalCode() {
        return loadingPostalCode;
    }

    public void setLoadingPostalCode(String loadingPostalCode) {
        this.loadingPostalCode = loadingPostalCode;
    }

    public String getUnloadingCountry() {
        return unloadingCountry;
    }

    public void setUnloadingCountry(String unloadingCountry) {
        this.unloadingCountry = unloadingCountry;
    }

    public String getUnloadingCity() {
        return unloadingCity;
    }

    public void setUnloadingCity(String unloadingCity) {
        this.unloadingCity = unloadingCity;
    }

    public String getUnloadingPostalCode() {
        return unloadingPostalCode;
    }

    public void setUnloadingPostalCode(String unloadingPostalCode) {
        this.unloadingPostalCode = unloadingPostalCode;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getLoadingMeters() {
        return loadingMeters;
    }

    public void setLoadingMeters(BigDecimal loadingMeters) {
        this.loadingMeters = loadingMeters;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public Integer getPallets() {
        return pallets;
    }

    public void setPallets(Integer pallets) {
        this.pallets = pallets;
    }

    public String getCargoType() {
        return cargoType;
    }

    public void setCargoType(String cargoType) {
        this.cargoType = cargoType;
    }

    public BigDecimal getClientPrice() {
        return clientPrice;
    }

    public void setClientPrice(BigDecimal clientPrice) {
        this.clientPrice = clientPrice;
    }

    public BigDecimal getCarrierPrice() {
        return carrierPrice;
    }

    public void setCarrierPrice(BigDecimal carrierPrice) {
        this.carrierPrice = carrierPrice;
    }

    public String getSpeditorPrice() {
        return speditorPrice;
    }

    public void setSpeditorPrice(String speditorPrice) {
        this.speditorPrice = speditorPrice;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public UUID getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(UUID carrierId) {
        this.carrierId = carrierId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public void setNewOfferAvailable(boolean newOfferAvailable) {
        this.newOfferAvailable = newOfferAvailable;
    }

    public boolean isTrailer() {
        return trailer;
    }

    public void setTrailer(boolean trailer) {
        this.trailer = trailer;
    }

    public boolean isHanger() {
        return hanger;
    }

    public void setHanger(boolean hanger) {
        this.hanger = hanger;
    }

    public boolean isRefrigerated() {
        return refrigerated;
    }

    public void setRefrigerated(boolean refrigerated) {
        this.refrigerated = refrigerated;
    }

    public boolean isDualTemperature() {
        return dualTemperature;
    }

    public void setDualTemperature(boolean dualTemperature) {
        this.dualTemperature = dualTemperature;
    }

    public boolean isVan() {
        return van;
    }

    public void setVan(boolean van) {
        this.van = van;
    }

    public boolean isMicrobus() {
        return microbus;
    }

    public void setMicrobus(boolean microbus) {
        this.microbus = microbus;
    }

    public boolean isClothingTruck() {
        return clothingTruck;
    }

    public void setClothingTruck(boolean clothingTruck) {
        this.clothingTruck = clothingTruck;
    }

    public boolean isSolo() {
        return solo;
    }

    public void setSolo(boolean solo) {
        this.solo = solo;
    }

    public boolean isJumbo() {
        return jumbo;
    }

    public void setJumbo(boolean jumbo) {
        this.jumbo = jumbo;
    }

    public boolean isTractor() {
        return tractor;
    }

    public void setTractor(boolean tractor) {
        this.tractor = tractor;
    }

    public boolean isPlatform() {
        return platform;
    }

    public void setPlatform(boolean platform) {
        this.platform = platform;
    }

    public boolean isFlatbed() {
        return flatbed;
    }

    public void setFlatbed(boolean flatbed) {
        this.flatbed = flatbed;
    }

    public boolean isContainerTruck() {
        return containerTruck;
    }

    public void setContainerTruck(boolean containerTruck) {
        this.containerTruck = containerTruck;
    }

    public boolean isContainer() {
        return container;
    }

    public void setContainer(boolean container) {
        this.container = container;
    }

    public boolean isTanker() {
        return tanker;
    }

    public void setTanker(boolean tanker) {
        this.tanker = tanker;
    }

    public boolean isMegatrailer() {
        return megatrailer;
    }

    public void setMegatrailer(boolean megatrailer) {
        this.megatrailer = megatrailer;
    }

    public boolean isCarCarrier() {
        return carCarrier;
    }

    public void setCarCarrier(boolean carCarrier) {
        this.carCarrier = carCarrier;
    }

    public boolean isGondola() {
        return gondola;
    }

    public void setGondola(boolean gondola) {
        this.gondola = gondola;
    }

    public boolean isOversized() {
        return oversized;
    }

    public void setOversized(boolean oversized) {
        this.oversized = oversized;
    }

    public boolean isTrailerOnly() {
        return trailerOnly;
    }

    public void setTrailerOnly(boolean trailerOnly) {
        this.trailerOnly = trailerOnly;
    }

    public boolean isTunnel() {
        return tunnel;
    }

    public void setTunnel(boolean tunnel) {
        this.tunnel = tunnel;
    }

    public boolean isTailLift() {
        return tailLift;
    }

    public void setTailLift(boolean tailLift) {
        this.tailLift = tailLift;
    }

    public boolean isCrane() {
        return crane;
    }

    public void setCrane(boolean crane) {
        this.crane = crane;
    }

    public boolean isTwoDrivers() {
        return twoDrivers;
    }

    public void setTwoDrivers(boolean twoDrivers) {
        this.twoDrivers = twoDrivers;
    }

    public boolean isPalletTruck() {
        return palletTruck;
    }

    public void setPalletTruck(boolean palletTruck) {
        this.palletTruck = palletTruck;
    }
}