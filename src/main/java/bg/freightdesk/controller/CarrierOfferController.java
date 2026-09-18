package bg.freightdesk.controller;

import bg.freightdesk.dto.CarrierOfferDTO;
import bg.freightdesk.service.CarrierOfferService;
import bg.freightdesk.service.CarrierService;
import bg.freightdesk.service.CompanyService;
import bg.freightdesk.service.FreightRequestService;
import bg.freightdesk.model.CompanyType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
public class CarrierOfferController {

    private final CarrierOfferService carrierOfferService;
    private final CarrierService carrierService;
    private final CompanyService companyService;
    private final FreightRequestService freightRequestService;

    public CarrierOfferController(
            CarrierOfferService carrierOfferService,
            CarrierService carrierService,
            CompanyService companyService,
            FreightRequestService freightRequestService) {

        this.carrierOfferService = carrierOfferService;
        this.carrierService = carrierService;
        this.companyService = companyService;
        this.freightRequestService = freightRequestService;
    }

    @GetMapping("/freight-requests/{freightRequestId}/offers")
    public String showOffers(
            @PathVariable UUID freightRequestId,
            Model model) {

        model.addAttribute(
                "freightRequest",
                freightRequestService.findDTOById(freightRequestId)
        );

        model.addAttribute(
                "offers",
                carrierOfferService.findByFreightRequestId(
                        freightRequestId
                )
        );

        model.addAttribute(
                "carrierCompanies",
                companyService.findAllByType(CompanyType.CARRIER)
        );

        model.addAttribute(
                "carrierContacts",
                java.util.List.of()
        );

        model.addAttribute(
                "carrierOffer",
                new CarrierOfferDTO()
        );

        return "carrier-offers";
    }

    @GetMapping("/carrier-offers/carriers")
    @ResponseBody
    public Object getCarrierContacts(
            @RequestParam UUID companyId) {

        return carrierService.findAllByCompany(companyId);
    }

    @PostMapping("/freight-requests/{freightRequestId}/offers")
    public String createOffer(
            @PathVariable UUID freightRequestId,
            @ModelAttribute CarrierOfferDTO dto) {


        System.out.println("OFFERED PRICE = " + dto.getOfferedPrice());
        System.out.println("COMPANY ID = " + dto.getCompanyId());
        System.out.println("CARRIER ID = " + dto.getCarrierId());

        dto.setFreightRequestId(freightRequestId);

        carrierOfferService.saveDTO(dto);

        return "redirect:/freight-requests/"
                + freightRequestId
                + "/offers";
    }

    @GetMapping("/carrier-offers/{id}/delete")
    public String deleteOffer(@PathVariable UUID id) {

        CarrierOfferDTO offer =
                carrierOfferService.findDTOById(id);

        UUID freightRequestId =
                offer.getFreightRequestId();

        carrierOfferService.delete(id);

        return "redirect:/freight-requests/"
                + freightRequestId
                + "/offers";
    }
}