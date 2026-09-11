package bg.freightdesk.controller;

import bg.freightdesk.dto.CarrierDTO;
import bg.freightdesk.dto.FreightRequestDTO;
import bg.freightdesk.model.CompanyType;
import bg.freightdesk.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
public class FreightRequestController {

    private final FreightRequestService freightRequestService;
    private final CustomerService customerService;
    private final CarrierService carrierService;
    private final CompanyService companyService;
    private final SpeditorBrowserService speditorBrowserService;
    private final CountryService countryService;

    public FreightRequestController(
            FreightRequestService freightRequestService,
            CustomerService customerService,
            CarrierService carrierService,
            CompanyService companyService,
            SpeditorBrowserService speditorBrowserService,
            CountryService countryService) {

        this.freightRequestService = freightRequestService;
        this.customerService = customerService;
        this.carrierService = carrierService;
        this.companyService = companyService;
        this.speditorBrowserService = speditorBrowserService;
        this.countryService = countryService;
    }

    @GetMapping("/freight-requests")
    public String showFreightRequests(Model model) {

        List<FreightRequestDTO> freightRequests =
                freightRequestService.findAll();

        model.addAttribute(
                "freightRequests",
                freightRequests
        );

        return "freight-requests";
    }

    @GetMapping("/freight-requests/new")
    public String showFreightRequestForm(Model model) {

        model.addAttribute(
                "freightRequest",
                new FreightRequestDTO()
        );

        model.addAttribute(
                "customers",
                customerService.findAll()
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
                "selectedCarrierCompanyId",
                null
        );

        model.addAttribute(
                "countries",
                countryService.findAll()
        );

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

        model.addAttribute(
                "freightRequest",
                dto
        );

        model.addAttribute(
                "customers",
                customerService.findAllForEdit(
                        dto.getCustomerId()
                )
        );

        model.addAttribute(
                "carrierCompanies",
                companyService.findAllByType(
                        CompanyType.CARRIER
                )
        );

        if (dto.getCarrierId() != null) {

            CarrierDTO currentCarrier =
                    carrierService.findDTOById(
                            dto.getCarrierId()
                    );

            model.addAttribute(
                    "carrierContacts",
                    carrierService.findAllByCompanyForEdit(
                            currentCarrier.getCompanyId(),
                            dto.getCarrierId()
                    )
            );

            model.addAttribute(
                    "selectedCarrierCompanyId",
                    currentCarrier.getCompanyId()
            );

        } else {

            model.addAttribute(
                    "carrierContacts",
                    List.of()
            );

            model.addAttribute(
                    "selectedCarrierCompanyId",
                    null
            );
        }

        model.addAttribute(
                "countries",
                countryService.findAll()
        );

        return "freight-request-form";
    }

    @GetMapping("/freight-requests/{id}/delete")
    public String deleteFreightRequest(
            @PathVariable UUID id) {

        freightRequestService.delete(id);

        return "redirect:/freight-requests";
    }

    @PostMapping("/freight-requests/{id}/publish-speditor")
    public String publishToSpeditor(
            @PathVariable UUID id) {

        speditorBrowserService.openSpeditor(id);

        return "redirect:/freight-requests";
    }

    @GetMapping("/freight-requests/carriers")
    @ResponseBody
    public List<CarrierDTO> getCarriersByCompany(
            @RequestParam UUID companyId) {

        return carrierService.findAllByCompany(companyId);
    }
}