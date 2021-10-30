package io.github.hejcz.web;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RedirectToIndexErrorController implements ErrorController {

    @RequestMapping("/error")
    public String error() {
        return "forward:/index.html";
    }

    public String getErrorPath() {
        return null;
    }
}
