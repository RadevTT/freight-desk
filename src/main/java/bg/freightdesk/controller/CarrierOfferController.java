
package bg.freightdesk.controller;

import bg.freightdesk.dto.CarrierOfferDTO;
import bg.freightdesk.entity.CompanyType;
import bg.freightdesk.service.CarrierOfferService;
import bg.freightdesk.service.CarrierService;
import bg.freightdesk.service.CompanyService;
import bg.freightdesk.service.FreightRequestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
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
                freightRequestService.findDTOById(freightRequestId));

        model.addAttribute(
                "offers",
                carrierOfferService.findByFreightRequestId(freightRequestId));

        model.addAttribute(
                "carrierCompanies",
                companyService.findAllByType(CompanyType.CARRIER));

        model.addAttribute("carrierContacts", List.of());
        model.addAttribute("carrierOffer", new CarrierOfferDTO());

        return "carrier-offers";
    }

    @GetMapping("/carrier-offers/carriers")
    @ResponseBody
    public Object getCarrierContacts(@RequestParam UUID companyId) {
        return carrierService.findAllByCompany(companyId);
    }

    @GetMapping("/carrier-offers/company")
    @ResponseBody
    public Object getCompany(@RequestParam UUID companyId) {
        return companyService.findDTOById(companyId);
    }

    @GetMapping("/carrier-offers/contact")
    @ResponseBody
    public Object getContact(@RequestParam UUID carrierId) {
        return carrierService.findDTOById(carrierId);
    }

    @PostMapping("/freight-requests/{freightRequestId}/offers")
    public String createOffer(
            @PathVariable UUID freightRequestId,
            @ModelAttribute("carrierOffer") CarrierOfferDTO carrierOffer) {

        carrierOffer.setFreightRequestId(freightRequestId);
        carrierOfferService.saveDTO(carrierOffer);

        return "redirect:/freight-requests/"
                + freightRequestId
                + "/offers";
    }

    @PostMapping("/carrier-offers/{id}/delete")
    public String deleteOffer(@PathVariable UUID id) {

        CarrierOfferDTO offer = carrierOfferService.findDTOById(id);
        UUID freightRequestId = offer.getFreightRequestId();

        carrierOfferService.delete(id);

        return "redirect:/freight-requests/"
                + freightRequestId
                + "/offers";
    }

    @PostMapping("/carrier-offers/{id}/select")
    public String selectOffer(@PathVariable UUID id) {

        CarrierOfferDTO offer = carrierOfferService.findDTOById(id);
        UUID freightRequestId = offer.getFreightRequestId();

        carrierOfferService.selectOffer(id);

        return "redirect:/freight-requests/"
                + freightRequestId
                + "/offers";
    }

    @GetMapping("/carrier-offers/{id}/edit")
    public String showEditForm(
            @PathVariable UUID id,
            Model model) {

        CarrierOfferDTO carrierOffer = carrierOfferService.findDTOById(id);

        model.addAttribute("carrierOffer", carrierOffer);

        model.addAttribute(
                "carrierCompanies",
                companyService.findAllByType(CompanyType.CARRIER));

        if (carrierOffer.getCompanyId() != null) {
            model.addAttribute(
                    "carrierContacts",
                    carrierService.findAllByCompany(
                            carrierOffer.getCompanyId()));
        } else {
            model.addAttribute("carrierContacts", List.of());
        }

        return "carrier-offer-form";
    }

    @PostMapping("/carrier-offers/{id}")
    public String updateOffer(
            @PathVariable UUID id,
            @ModelAttribute("carrierOffer") CarrierOfferDTO carrierOffer) {

        carrierOffer.setId(id);
        carrierOfferService.updateDTO(carrierOffer);

        return "redirect:/freight-requests/"
                + carrierOffer.getFreightRequestId()
                + "/offers";
    }
}
