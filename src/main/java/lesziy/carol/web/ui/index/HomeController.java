package lesziy.carol.web.ui.index;

import lesziy.carol.web.ui.login.AuthenticationPredicates;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
class HomeController {

    private final AuthenticationPredicates authenticationPredicates =
        new AuthenticationPredicates();

    @GetMapping
    String index() {
        if (authenticationPredicates.isAlreadyLogged()) {
            return "redirect:/dashboard";
        }
        return "index";
    }
}
