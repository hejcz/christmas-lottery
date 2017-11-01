package io.github.hejcz.web.ui.registration;

import io.github.hejcz.domain.registration.RegistrationFacade;
import io.github.hejcz.domain.user.DtoUser;
import io.github.hejcz.domain.user.SystemRole;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/register")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class RegistrationController {

    private final RegistrationFacade registrationFacade;

    @GetMapping
    public String register(RegistrationForm form) {
        return "registration";
    }

    @PostMapping
    public String register(@ModelAttribute RegistrationForm registrationForm,
                           Errors errors) {
        if (errors.hasErrors()) {
            return "registration";
        }

        System.out.println(registrationForm);

        DtoUser dtoUser = new DtoUser();
        dtoUser.login(registrationForm.getUsername());
        dtoUser.name(registrationForm.getName());
        dtoUser.surname(registrationForm.getSurname());
        dtoUser.password(registrationForm.getPassword());
        dtoUser.email(registrationForm.getEmail());
        dtoUser.systemRole(SystemRole.USER);

        try {
            registrationFacade.register(dtoUser);
        } catch (Exception ignored) {
            return "registration";
        }

        return "redirect:/login.html";
    }

}
