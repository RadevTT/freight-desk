package bg.freightdesk.controller;

import bg.freightdesk.dto.CompanyDTO;
import bg.freightdesk.model.CompanyType;
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
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/companies")
    public String showCompanies(Model model) {

        List<CompanyDTO> companies =
                companyService.findAll();

        model.addAttribute("companies", companies);

        return "companies";
    }

    @GetMapping("/companies/new")
    public String showCompanyForm(Model model) {

        model.addAttribute("company", new CompanyDTO());

        model.addAttribute("companyTypes", CompanyType.values());

        return "company-form";
    }

    @PostMapping("/companies")
    public String saveCompany(
            @ModelAttribute CompanyDTO dto) {

        companyService.saveDTO(dto);

        return "redirect:/companies";
    }

    @GetMapping("/companies/edit/{id}")
    public String showEditForm(
            @PathVariable UUID id,
            Model model) {

        CompanyDTO dto =
                companyService.findDTOById(id);

        model.addAttribute("company", dto);

        model.addAttribute("companyTypes", CompanyType.values());

        return "company-form";
    }

    @PostMapping("/companies/delete/{id}")
    public String deleteCompany(@PathVariable UUID id) {

        companyService.delete(id);

        return "redirect:/companies";
    }
}

