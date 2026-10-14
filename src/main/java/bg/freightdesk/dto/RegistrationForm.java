package bg.freightdesk.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegistrationForm {

    @NotBlank(message = "Въведи име на организацията.")
    @Size(max = 150, message = "Името на организацията е твърде дълго.")
    private String organizationName;

    @NotBlank(message = "Въведи потребителско име.")
    @Size(min = 3, max = 50,
            message = "Потребителското име трябва да е между 3 и 50 символа.")
    private String username;

    @NotBlank(message = "Въведи собствено име.")
    private String firstName;

    @NotBlank(message = "Въведи фамилия.")
    private String lastName;

    @NotBlank(message = "Въведи имейл адрес.")
    @Email(message = "Въведи валиден имейл адрес.")
    private String email;

    @NotBlank(message = "Въведи парола.")
    @Size(min = 8, message = "Паролата трябва да е поне 8 символа.")
    private String password;

    @NotBlank(message = "Потвърди паролата.")
    private String confirmPassword;

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}