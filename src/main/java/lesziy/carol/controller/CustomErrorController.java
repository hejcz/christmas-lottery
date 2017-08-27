package lesziy.carol.controller;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Przy próbie dostępu do nieistniejącego zasobu statycznego przekierowuje
 * na stronę główną.
 */
@Controller
class CustomErrorController implements ErrorController {

    private static final String ERROR_PATH = "/error";;

    @RequestMapping(ERROR_PATH)
    public String index() {
        return "redirect:/";
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }
}
