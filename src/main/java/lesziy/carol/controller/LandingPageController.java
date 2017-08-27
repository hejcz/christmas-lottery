package lesziy.carol.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.websocket.server.PathParam;

/**
 * Obsługuje wszystkie URLe, w któróch ostatniej części ścieżki do zasobu
 * nie ma kropki. Dzięki temu strona obsługuje dostęp do js, css etc.
 */
@Controller
class LandingPageController {

    @RequestMapping(value = "/**/{[path:[^\\.]*}")
    public String index(@PathParam("path") final String path) {
        return "forward:/index.html";
    }
}
