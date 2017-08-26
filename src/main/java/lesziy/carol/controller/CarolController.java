package lesziy.carol.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
class CarolController {
    private static Logger logger = Logger.getLogger(CarolController.class.getName());

    @RequestMapping("/wot")
    public String wotPage() {
        logger.info("u wot m8!?");
        return "index";
    }

    @RequestMapping(value = "/")
    public String homeSite() {
        logger.info("home site");
        return "index";
    }
}
