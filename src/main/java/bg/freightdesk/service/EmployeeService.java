
package bg.freightdesk.service;

import bg.freightdesk.dto.EmployeeForm;
import bg.freightdesk.entity.User;
import bg.freightdesk.model.Role;
import bg.freightdesk.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EmployeeService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private User getCurrentAdmin() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            throw new AccessDeniedException(
                    "Трябва да влезете в системата.");
        }

        User currentUser = userRepository
                .findByUsername(authentication.getName())
                .orElseThrow(() ->
                        new AccessDeniedException(
                                "Потребителят не е намерен."));

        if (currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException(
                    "Само администратор може да управлява служителите.");
        }

        return currentUser;
    }

    public void checkAdminAccess() {
        getCurrentAdmin();
    }

    @Transactional(readOnly = true)
    public List<User> getEmployees() {
        User admin = getCurrentAdmin();

        return userRepository
                .findAllByOrganization_Id(
                        admin.getOrganization().getId())
                .stream()
                .filter(user -> user.getRole() == Role.FORWARDER)
                .toList();
    }

    @Transactional
    public User createEmployee(EmployeeForm form) {
        User admin = getCurrentAdmin();

        if (!form.getPassword().equals(form.getConfirmPassword())) {
            throw new IllegalArgumentException(
                    "Паролата и потвърждението не съвпадат.");
        }

        String username = form.getUsername().trim();
        String email = form.getEmail().trim().toLowerCase();

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException(
                    "Това потребителско име вече е заето.");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Този имейл адрес вече е регистриран.");
        }

        User employee = new User();
        employee.setUsername(username);
        employee.setPassword(
                passwordEncoder.encode(form.getPassword()));
        employee.setFirstName(form.getFirstName().trim());
        employee.setLastName(form.getLastName().trim());
        employee.setEmail(email);
        employee.setEnabled(true);
        employee.setRole(Role.FORWARDER);
        employee.setOrganization(admin.getOrganization());
        employee.setVisibleToColleagues(true);

        return userRepository.save(employee);
    }

    @Transactional
    public void setEmployeeEnabled(UUID employeeId, boolean enabled) {
        User admin = getCurrentAdmin();

        User employee = userRepository.findById(employeeId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Служителят не е намерен."));

        if (!employee.getOrganization().getId()
                .equals(admin.getOrganization().getId())) {
            throw new AccessDeniedException(
                    "Нямате достъп до този служител.");
        }

        if (employee.getRole() != Role.FORWARDER) {
            throw new AccessDeniedException(
                    "Може да управлявате само служители.");
        }

        employee.setEnabled(enabled);
        userRepository.save(employee);
    }
}
