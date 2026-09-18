package bg.freightdesk.controller;

import bg.freightdesk.dto.CarrierDTO;
import bg.freightdesk.model.CompanyType;
import bg.freightdesk.service.CarrierService;
import bg.freightdesk.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Controller
public class CarrierController {

    private final CarrierService carrierService;
    private final CompanyService companyService;

    public CarrierController(
            CarrierService carrierService,
            CompanyService companyService) {

        this.carrierService = carrierService;
        this.companyService = companyService;
    }

    @GetMapping("/carriers")
    public String showCarriers(Model model) {

        List<CarrierDTO> carriers =
                carrierService.findAll();

        model.addAttribute("carriers", carriers);

        return "carriers";
    }

    @GetMapping("/carriers/new")
    public String showCarrierForm(
            @RequestParam(required = false) UUID companyId,
            Model model) {

        CarrierDTO carrier = new CarrierDTO();

        if (companyId != null) {
            carrier.setCompanyId(companyId);
        }

        model.addAttribute(
                "carrier",
                carrier
        );

        model.addAttribute(
                "companies",
                companyService.findAllByType(CompanyType.CARRIER)
        );

        return "carrier-form";
    }

    @PostMapping("/carriers")
    public String saveCarrier(
            @Valid @ModelAttribute("carrier") CarrierDTO dto,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {

            model.addAttribute(
                    "companies",
                    companyService.findAllByType(CompanyType.CARRIER)
            );

            return "carrier-form";
        }

        carrierService.saveDTO(dto);

        return "redirect:/carriers";
    }

    @GetMapping("/carriers/edit/{id}")
    public String showEditForm(
            @PathVariable UUID id,
            Model model) {

        CarrierDTO dto =
                carrierService.findDTOById(id);

        model.addAttribute("carrier", dto);

        model.addAttribute(
                "companies",
                companyService.findAllByType(CompanyType.CARRIER)
        );

        return "carrier-form";
    }

    @PostMapping("/carriers/deactivate/{id}")
    public String deactivateCarrier(
            @PathVariable UUID id,
            @RequestParam(required = false) UUID companyId) {

        carrierService.deactivate(id);

        if (companyId != null) {
            return "redirect:/companies/edit/" + companyId;
        }

        return "redirect:/carriers";
    }

    @GetMapping("/carriers/inactive")
    public String showInactiveCarriers(Model model) {

        List<CarrierDTO> carriers =
                carrierService.findInactive();

        model.addAttribute("carriers", carriers);

        return "inactive-carriers";
    }

    @PostMapping("/carriers/activate/{id}")
    public String activateCarrier(
            @PathVariable UUID id,
            @RequestParam(required = false) UUID companyId) {

        carrierService.activate(id);

        if (companyId != null) {
            return "redirect:/companies/edit/" + companyId;
        }

        return "redirect:/carriers/inactive";
    }
}

