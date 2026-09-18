package bg.freightdesk.controller;

import bg.freightdesk.dto.CarrierDTO;
import bg.freightdesk.dto.CompanyDTO;
import bg.freightdesk.dto.CompanyFormDTO;
import bg.freightdesk.dto.CustomerDTO;
import bg.freightdesk.model.CompanyType;
import bg.freightdesk.service.CarrierService;
import bg.freightdesk.service.CompanyService;
import bg.freightdesk.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.UUID;

@Controller
public class CompanyController {

    private final CompanyService companyService;
    private final CustomerService customerService;
    private final CarrierService carrierService;

    public CompanyController(
            CompanyService companyService,
            CustomerService customerService,
            CarrierService carrierService) {

        this.companyService = companyService;
        this.customerService = customerService;
        this.carrierService = carrierService;
    }

    @GetMapping("/companies")
    public String showCompanies(Model model) {

        List<CompanyDTO> companies =
                companyService.findAll();

        model.addAttribute(
                "companies",
                companies
        );

        return "companies";
    }

    @GetMapping("/companies/new")
    public String showCompanyForm(Model model) {

        model.addAttribute(
                "company",
                new CompanyFormDTO()
        );

        model.addAttribute(
                "companyTypes",
                CompanyType.values()
        );

        model.addAttribute(
                "customers",
                List.of()
        );

        model.addAttribute(
                "carriers",
                List.of()
        );

        model.addAttribute(
                "inactiveCustomers",
                List.of()
        );

        model.addAttribute(
                "inactiveCarriers",
                List.of()
        );

        return "company-form";
    }

    @PostMapping("/companies")
    public String saveCompany(
            @Valid @ModelAttribute("company") CompanyFormDTO dto,
            BindingResult bindingResult,
            Model model) {

        if (dto.getContactName() == null
                || dto.getContactName().isBlank()) {

            bindingResult.rejectValue(
                    "contactName",
                    "required",
                    "Contact name is required"
            );
        }

        if (bindingResult.hasErrors()) {

            model.addAttribute(
                    "companyTypes",
                    CompanyType.values()
            );

            model.addAttribute(
                    "customers",
                    List.of()
            );

            model.addAttribute(
                    "carriers",
                    List.of()
            );

            model.addAttribute(
                    "inactiveCustomers",
                    List.of()
            );

            model.addAttribute(
                    "inactiveCarriers",
                    List.of()
            );

            return "company-form";
        }

        CompanyDTO companyDTO =
                new CompanyDTO();

        companyDTO.setName(dto.getName());
        companyDTO.setCompanyCode(dto.getCompanyCode());
        companyDTO.setCountry(dto.getCountry());
        companyDTO.setCity(dto.getCity());
        companyDTO.setType(dto.getType());
        companyDTO.setActive(true);

        CompanyDTO savedCompany =
                companyService.saveDTO(companyDTO);

        if (dto.getType() == CompanyType.CUSTOMER) {

            CustomerDTO customer =
                    new CustomerDTO();

            customer.setName(dto.getContactName());
            customer.setEmail(dto.getContactEmail());
            customer.setPhone(dto.getContactPhone());

            customer.setCompanyId(
                    savedCompany.getId()
            );

            customerService.saveDTO(customer);

        } else if (dto.getType() == CompanyType.CARRIER) {

            CarrierDTO carrier =
                    new CarrierDTO();

            carrier.setName(dto.getContactName());
            carrier.setEmail(dto.getContactEmail());
            carrier.setPhone(dto.getContactPhone());

            carrier.setCompanyId(
                    savedCompany.getId()
            );

            carrierService.saveDTO(carrier);
        }

        return "redirect:/companies";
    }

    @GetMapping("/companies/edit/{id}")
    public String showEditForm(
            @PathVariable UUID id,
            Model model) {

        CompanyDTO company =
                companyService.findDTOById(id);

        model.addAttribute(
                "company",
                company
        );

        model.addAttribute(
                "companyTypes",
                CompanyType.values()
        );

        if (company.getType() == CompanyType.CUSTOMER) {

            model.addAttribute(
                    "customers",
                    customerService.findAllByCompany(id)
            );

            model.addAttribute(
                    "carriers",
                    List.of()
            );

            model.addAttribute(
                    "inactiveCustomers",
                    customerService.findInactiveByCompany(id)
            );

            model.addAttribute(
                    "inactiveCarriers",
                    List.of()
            );

        } else if (company.getType() == CompanyType.CARRIER) {

            model.addAttribute(
                    "customers",
                    List.of()
            );

            model.addAttribute(
                    "carriers",
                    carrierService.findAllByCompany(id)
            );

            model.addAttribute(
                    "inactiveCustomers",
                    List.of()
            );

            model.addAttribute(
                    "inactiveCarriers",
                    carrierService.findInactiveByCompany(id)
            );

        } else {

            model.addAttribute(
                    "customers",
                    List.of()
            );

            model.addAttribute(
                    "carriers",
                    List.of()
            );

            model.addAttribute(
                    "inactiveCustomers",
                    List.of()
            );

            model.addAttribute(
                    "inactiveCarriers",
                    List.of()
            );
        }

        return "company-form";
    }

    @PostMapping("/companies/edit/{id}")
    public String updateCompany(
            @PathVariable UUID id,
            @Valid @ModelAttribute("company") CompanyDTO dto,
            BindingResult bindingResult,
            Model model) {

        dto.setId(id);

        if (bindingResult.hasErrors()) {

            model.addAttribute(
                    "companyTypes",
                    CompanyType.values()
            );

            if (dto.getType() == CompanyType.CUSTOMER) {

                model.addAttribute(
                        "customers",
                        customerService.findAllByCompany(id)
                );

                model.addAttribute(
                        "carriers",
                        List.of()
                );

                model.addAttribute(
                        "inactiveCustomers",
                        customerService.findInactiveByCompany(id)
                );

                model.addAttribute(
                        "inactiveCarriers",
                        List.of()
                );

            } else if (dto.getType() == CompanyType.CARRIER) {

                model.addAttribute(
                        "customers",
                        List.of()
                );

                model.addAttribute(
                        "carriers",
                        carrierService.findAllByCompany(id)
                );

                model.addAttribute(
                        "inactiveCustomers",
                        List.of()
                );

                model.addAttribute(
                        "inactiveCarriers",
                        carrierService.findInactiveByCompany(id)
                );

            } else {

                model.addAttribute(
                        "customers",
                        List.of()
                );

                model.addAttribute(
                        "carriers",
                        List.of()
                );

                model.addAttribute(
                        "inactiveCustomers",
                        List.of()
                );

                model.addAttribute(
                        "inactiveCarriers",
                        List.of()
                );
            }

            return "company-form";
        }

        companyService.saveDTO(dto);

        return "redirect:/companies";
    }

    @PostMapping("/companies/deactivate/{id}")
    public String deactivateCompany(
            @PathVariable UUID id) {

        companyService.deactivate(id);

        return "redirect:/companies";
    }

    @GetMapping("/companies/inactive")
    public String showInactiveCompanies(Model model) {

        List<CompanyDTO> companies =
                companyService.findInactive();

        model.addAttribute(
                "companies",
                companies
        );

        return "inactive-companies";
    }

    @PostMapping("/companies/activate/{id}")
    public String activateCompany(
            @PathVariable UUID id) {

        companyService.activate(id);

        return "redirect:/companies/inactive";
    }
}

