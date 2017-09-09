package lesziy.carol.web.ui.registration;

import lesziy.carol.domain.registration.RegistrationFacade;
import lesziy.carol.domain.user.DtoUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

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
    public String register(@Valid RegistrationForm registrationForm,
                           Errors errors) {
        if (errors.hasErrors()) {
            return "registration";
        }

        DtoUser dtoUser = new DtoUser();
        dtoUser.login(registrationForm.getUsername());
        dtoUser.password(registrationForm.getPassword());
        dtoUser.email(registrationForm.getEmail());
        registrationFacade.register(dtoUser);

        return "redirect:/login.html";
    }

}
