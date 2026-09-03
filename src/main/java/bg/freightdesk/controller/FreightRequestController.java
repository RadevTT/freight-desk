package bg.freightdesk.controller;

import bg.freightdesk.dto.FreightRequestDTO;
import bg.freightdesk.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.UUID;

@Controller
public class FreightRequestController {

    private final FreightRequestService freightRequestService;
    private final CustomerService customerService;
    private final CarrierService carrierService;
    private final SpeditorBrowserService speditorBrowserService;
    private final CountryService countryService;

    public FreightRequestController(
            FreightRequestService freightRequestService,
            CustomerService customerService,
            CarrierService carrierService, SpeditorBrowserService speditorBrowserService, CountryService countryService) {

        this.freightRequestService = freightRequestService;
        this.customerService = customerService;
        this.carrierService = carrierService;
        this.speditorBrowserService = speditorBrowserService;
        this.countryService = countryService;
    }

    @GetMapping("/freight-requests")
    public String showFreightRequests(Model model) {

        List<FreightRequestDTO> freightRequests =
                freightRequestService.findAll();

        model.addAttribute("freightRequests", freightRequests);

        return "freight-requests";
    }

    @GetMapping("/freight-requests/new")
    public String showFreightRequestForm(Model model) {

        model.addAttribute("freightRequest", new FreightRequestDTO());
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("carriers", carrierService.findAll());
        model.addAttribute("countries", countryService.findAll());

        return "freight-request-form";
    }

    @PostMapping("/freight-requests")
    public String createFreightRequest(
            @ModelAttribute FreightRequestDTO dto) {

        freightRequestService.saveDTO(dto);

        return "redirect:/freight-requests";
    }

    @GetMapping("/freight-requests/{id}/edit")
    public String showEditForm(
            @PathVariable UUID id,
            Model model) {

        FreightRequestDTO dto =
                freightRequestService.findDTOById(id);

        model.addAttribute("freightRequest", dto);
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("carriers", carrierService.findAll());
        model.addAttribute("countries", countryService.findAll());

        return "freight-request-form";
    }

    @GetMapping("/freight-requests/{id}/delete")
    public String deleteFreightRequest(@PathVariable UUID id) {

        freightRequestService.delete(id);

        return "redirect:/freight-requests";
    }

    @PostMapping("/freight-requests/{id}/publish-speditor")
    public String publishToSpeditor(@PathVariable UUID id) {

        speditorBrowserService.openSpeditor(id);

        return "redirect:/freight-requests";
    }
}