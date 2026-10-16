package bg.freightdesk.controller;

import bg.freightdesk.model.ColleagueAccessLevel;
import bg.freightdesk.entity.User;
import bg.freightdesk.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class ProfileController {

    private final UserRepository userRepository;

    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private User getCurrentUser(Principal principal) {
        if (principal == null) {
            throw new AccessDeniedException("Необходим е вход в системата.");
        }

        return userRepository.findByUsername(principal.getName())
                .filter(User::isEnabled)
                .orElseThrow(() ->
                        new AccessDeniedException(
                                "Потребителят не е активен или не съществува."
                        ));
    }

    @GetMapping("/profile")
    public String profile() {
        return "redirect:/profile/access";
    }

    @GetMapping("/profile/access")
    public String accessSettings(Principal principal, Model model) {
        User user = getCurrentUser(principal);

        model.addAttribute("user", user);
        model.addAttribute(
                "accessLevels",
                ColleagueAccessLevel.values()
        );

        return "profile-access";
    }

    @PostMapping("/profile/access")
    public String saveAccessSettings(
            Principal principal,
            @RequestParam(
                    name = "visibleToColleagues",
                    defaultValue = "false"
            ) boolean visibleToColleagues,
            @RequestParam ColleagueAccessLevel colleagueAccessLevel,
            RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(principal);

        user.setVisibleToColleagues(visibleToColleagues);
        user.setColleagueAccessLevel(colleagueAccessLevel);

        userRepository.save(user);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Настройките за достъп са запазени успешно."
        );

        return "redirect:/profile/access";
    }
}