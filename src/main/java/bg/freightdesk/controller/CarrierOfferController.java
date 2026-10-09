package bg.freightdesk.controller;

import bg.freightdesk.dto.CarrierDTO;
import bg.freightdesk.dto.CarrierOfferDTO;
import bg.freightdesk.model.CompanyType;
import bg.freightdesk.service.CarrierOfferService;
import bg.freightdesk.service.CarrierService;
import bg.freightdesk.service.CompanyService;
import bg.freightdesk.service.FreightRequestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
                companyService.findAllByType(
                        CompanyType.CARRIER
                )
        );

        model.addAttribute(
                "carrierContacts",
                List.of()
        );

        model.addAttribute(
                "carrierOffer",
                new CarrierOfferDTO()
        );

        return "carrier-offers";
    }

    @GetMapping("/carrier-offers/carriers")
    @ResponseBody
    public List<CarrierDTO> getCarrierContacts(
            @RequestParam UUID companyId) {

        return carrierService.findAllByCompany(companyId);
    }

    @GetMapping("/carrier-offers/company")
    @ResponseBody
    public bg.freightdesk.dto.CompanyDTO getCompany(
            @RequestParam UUID companyId) {

        return companyService.findDTOById(companyId);
    }

    @GetMapping("/carrier-offers/contact")
    @ResponseBody
    public CarrierDTO getContact(
            @RequestParam UUID carrierId) {

        return carrierService.findDTOById(carrierId);
    }

    @PostMapping("/freight-requests/{freightRequestId}/offers")
    public String createOffer(
            @PathVariable UUID freightRequestId,
            @ModelAttribute CarrierOfferDTO dto) {

        System.out.println(
                "OFFERED PRICE = "
                        + dto.getOfferedPrice()
        );

        System.out.println(
                "COMPANY ID = "
                        + dto.getCompanyId()
        );

        System.out.println(
                "CARRIER ID = "
                        + dto.getCarrierId()
        );

        dto.setFreightRequestId(freightRequestId);

        carrierOfferService.saveDTO(dto);

        return "redirect:/freight-requests/"
                + freightRequestId
                + "/offers";
    }

    @GetMapping("/carrier-offers/{id}/delete")
    public String deleteOffer(
            @PathVariable UUID id) {

        CarrierOfferDTO offer =
                carrierOfferService.findDTOById(id);

        UUID freightRequestId =
                offer.getFreightRequestId();

        freightRequestService.deleteCarrierOffer(id);

        return "redirect:/freight-requests/"
                + freightRequestId
                + "/offers";
    }

    @GetMapping("/carrier-offers/{id}/select")
    public String selectOffer(
            @PathVariable UUID id) {

        CarrierOfferDTO offer =
                carrierOfferService.findDTOById(id);

        UUID freightRequestId =
                offer.getFreightRequestId();

        carrierOfferService.selectOffer(id);

        return "redirect:/freight-requests/"
                + freightRequestId
                + "/offers";
    }

    @GetMapping("/carrier-offers/{id}/edit")
    public String showEditForm(
            @PathVariable UUID id,
            Model model) {

        CarrierOfferDTO carrierOffer =
                carrierOfferService.findDTOById(id);

        model.addAttribute(
                "carrierOffer",
                carrierOffer
        );

        model.addAttribute(
                "carrierCompanies",
                companyService.findAllByType(
                        CompanyType.CARRIER
                )
        );

        if (carrierOffer.getCompanyId() != null) {

            model.addAttribute(
                    "carrierContacts",
                    carrierService.findAllByCompanyForEdit(
                            carrierOffer.getCompanyId(),
                            carrierOffer.getCarrierId()
                    )
            );

        } else {

            model.addAttribute(
                    "carrierContacts",
                    List.of()
            );
        }

        return "carrier-offer-form";
    }

    @PostMapping("/carrier-offers/{id}")
    public String updateOffer(
            @PathVariable UUID id,
            @ModelAttribute CarrierOfferDTO dto) {

        dto.setId(id);

        carrierOfferService.updateDTO(dto);

        return "redirect:/freight-requests/"
                + dto.getFreightRequestId()
                + "/offers";
    }
}