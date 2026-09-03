package bg.freightdesk.controller;

import bg.freightdesk.dto.CarrierOfferDTO;
import bg.freightdesk.service.CarrierOfferService;
import bg.freightdesk.service.CarrierService;
import bg.freightdesk.service.FreightRequestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
public class CarrierOfferController {

    private final CarrierOfferService carrierOfferService;
    private final CarrierService carrierService;
    private final FreightRequestService freightRequestService;

    public CarrierOfferController(
            CarrierOfferService carrierOfferService,
            CarrierService carrierService,
            FreightRequestService freightRequestService) {

        this.carrierOfferService = carrierOfferService;
        this.carrierService = carrierService;
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
                "carriers",
                carrierService.findAll()
        );

        model.addAttribute(
                "carrierOffer",
                new CarrierOfferDTO()
        );

        return "carrier-offers";
    }

    @PostMapping("/freight-requests/{freightRequestId}/offers")
    public String createOffer(
            @PathVariable UUID freightRequestId,
            @ModelAttribute CarrierOfferDTO dto) {

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