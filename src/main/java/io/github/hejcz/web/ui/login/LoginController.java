package io.github.hejcz.web.ui.login;

import io.github.hejcz.domain.user.UserSecurityFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Controller
@RequestMapping("/login")
@RequiredArgsConstructor
class LoginController {

    private final AuthenticationPredicates authenticationPredicates =
        new AuthenticationPredicates();

    private final UserSecurityFacade userSecurityFacade;

    private final HttpServletRequest httpRequest;

    @GetMapping
    String login() {
        return redirectIfLoggedOr("login");
    }

    @GetMapping("/password")
    String passwordRecovery() {
        return redirectIfLoggedOr("password_recovery");
    }

    @PostMapping("/password")
    @Transactional
    public String recover(PasswordRecoveryRequestForm passwordRecoveryRequestForm) {
        userSecurityFacade.requestPasswordRecovery(httpRequest.getRequestURL().toString(),
            passwordRecoveryRequestForm.getEmail());
        return "redirect:/login";
    }

    @GetMapping("/password/recover")
    public String recover(@RequestParam("token") String token) {
        return userSecurityFacade.recoveryEmail(token)
            .map(email -> {
                httpRequest.getSession().setAttribute("recovery_email", email);
                return "change_password";
            }).orElse("redirect:/login");
    }

    @PostMapping("/password/change")
    public String changePassword(NewPasswordForm newPasswordForm) {
        if (Objects.equals(newPasswordForm.getPassword(), newPasswordForm.getRepeatedPassword())
            && newPasswordForm.getPassword() != null) {
            userSecurityFacade.newPassword(
                httpRequest.getSession().getAttribute("recovery_email").toString(),
                newPasswordForm.getPassword()
            );
        }
        return "redirect:/login";
    }

    private String redirectIfLoggedOr(String notLoggedView) {
        if (authenticationPredicates.isAlreadyLogged()) {
            return "redirect:/dashboard";
        }
        return notLoggedView;
    }

}
