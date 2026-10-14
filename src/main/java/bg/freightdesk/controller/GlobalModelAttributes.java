package bg.freightdesk.controller;

import bg.freightdesk.entity.User;
import bg.freightdesk.repository.UserRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    private final UserRepository userRepository;

    public GlobalModelAttributes(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @ModelAttribute
    public void addCurrentUserDetails(Model model) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return;
        }

        userRepository.findByUsername(authentication.getName())
                .filter(User::isEnabled)
                .ifPresent(user -> {
                    model.addAttribute("currentUser", user);
                    model.addAttribute(
                            "currentOrganization",
                            user.getOrganization()
                    );
                });
    }
}