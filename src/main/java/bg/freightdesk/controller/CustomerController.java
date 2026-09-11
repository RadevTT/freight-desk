package bg.freightdesk.controller;

import bg.freightdesk.dto.CustomerDTO;
import bg.freightdesk.model.CompanyType;
import bg.freightdesk.service.CompanyService;
import bg.freightdesk.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.UUID;

@Controller
public class CustomerController {

    private final CustomerService customerService;
    private final CompanyService companyService;

    public CustomerController(
            CustomerService customerService,
            CompanyService companyService) {

        this.customerService = customerService;
        this.companyService = companyService;
    }

    @GetMapping("/customers")
    public String showCustomers(Model model) {

        List<CustomerDTO> customers =
                customerService.findAll();

        model.addAttribute("customers", customers);

        return "customers";
    }

    @GetMapping("/customers/new")
    public String showCustomerForm(Model model) {

        model.addAttribute("customer", new CustomerDTO());

        model.addAttribute(
                "companies",
                companyService.findAllByType(CompanyType.CUSTOMER)
        );

        return "customer-form";
    }

    @PostMapping("/customers")
    public String saveCustomer(
            @ModelAttribute CustomerDTO dto) {

        customerService.saveDTO(dto);

        return "redirect:/customers";
    }

    @GetMapping("/customers/edit/{id}")
    public String showEditForm(
            @PathVariable UUID id,
            Model model) {

        CustomerDTO dto =
                customerService.findDTOById(id);

        model.addAttribute("customer", dto);

        model.addAttribute(
                "companies",
                companyService.findAllByType(CompanyType.CUSTOMER)
        );

        return "customer-form";
    }

    @PostMapping("/customers/deactivate/{id}")
    public String deactivateCustomer(@PathVariable UUID id) {

        customerService.deactivate(id);

        return "redirect:/customers";
    }

    @GetMapping("/customers/inactive")
    public String showInactiveCustomers(Model model) {

        List<CustomerDTO> customers =
                customerService.findInactive();

        model.addAttribute("customers", customers);

        return "inactive-customers";
    }

    @PostMapping("/customers/activate/{id}")
    public String activateCustomer(@PathVariable UUID id) {

        customerService.activate(id);

        return "redirect:/customers/inactive";
    }
}

