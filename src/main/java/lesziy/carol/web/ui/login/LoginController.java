package lesziy.carol.web.ui.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
class LoginController {

    private final AuthenticationPredicates authenticationPredicates =
        new AuthenticationPredicates();

    @GetMapping
    String login() {
        if (authenticationPredicates.isAlreadyLogged()) {
            return "redirect:/dashboard";
        }
        return "login";
    }

    private boolean isAlreadyLogged() {
        return authenticationPredicates.isAlreadyLogged();
    }
}
