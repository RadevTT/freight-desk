
package bg.freightdesk.controller;

import bg.freightdesk.dto.EmployeeForm;
import bg.freightdesk.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public String listEmployees(Model model) {
        model.addAttribute(
                "employees", employeeService.getEmployees());

        return "employees";
    }

    @GetMapping("/new")
    public String newEmployeeForm(Model model) {
        employeeService.checkAdminAccess();

        model.addAttribute("employeeForm", new EmployeeForm());

        return "employee-form";
    }

    @PostMapping
    public String createEmployee(
            @Valid @ModelAttribute("employeeForm")
            EmployeeForm employeeForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        employeeService.checkAdminAccess();

        if (bindingResult.hasErrors()) {
            return "employee-form";
        }

        try {
            employeeService.createEmployee(employeeForm);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Служителят е създаден успешно.");

            return "redirect:/employees";

        } catch (IllegalArgumentException exception) {
            model.addAttribute("formError", exception.getMessage());
            return "employee-form";
        }
    }

    @PostMapping("/{id}/enabled")
    public String changeEmployeeStatus(
            @PathVariable UUID id,
            @RequestParam boolean enabled,
            RedirectAttributes redirectAttributes) {

        employeeService.setEmployeeEnabled(id, enabled);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                enabled
                        ? "Служителят е активиран."
                        : "Служителят е деактивиран.");

        return "redirect:/employees";
    }
}
