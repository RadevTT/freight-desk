package bg.freightdesk.controller;

import bg.freightdesk.dto.CarrierDTO;
import bg.freightdesk.service.CarrierService;
import bg.freightdesk.service.CompanyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
    public String showCarrierForm(Model model) {

        model.addAttribute("carrier", new CarrierDTO());
        model.addAttribute("companies", companyService.findAll());

        return "carrier-form";
    }

    @PostMapping("/carriers")
    public String saveCarrier(
            @ModelAttribute CarrierDTO dto) {

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
        model.addAttribute("companies", companyService.findAll());

        return "carrier-form";
    }

    @PostMapping("/carriers/delete/{id}")
    public String deleteCarrier(@PathVariable UUID id) {

        carrierService.delete(id);

        return "redirect:/carriers";
    }
}