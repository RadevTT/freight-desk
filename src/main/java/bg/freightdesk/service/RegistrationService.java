package bg.freightdesk.service;

import bg.freightdesk.dto.RegistrationForm;
import bg.freightdesk.entity.Organization;
import bg.freightdesk.entity.User;
import bg.freightdesk.model.Role;
import bg.freightdesk.repository.OrganizationRepository;
import bg.freightdesk.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(
            OrganizationRepository organizationRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(RegistrationForm form) {

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

        Organization organization = new Organization();
        organization.setName(form.getOrganizationName().trim());
        organization.setActive(true);

        Organization savedOrganization =
                organizationRepository.save(organization);

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setFirstName(form.getFirstName().trim());
        user.setLastName(form.getLastName().trim());
        user.setEmail(email);
        user.setEnabled(true);
        user.setRole(Role.ADMIN);
        user.setOrganization(savedOrganization);

        return userRepository.save(user);
    }
}