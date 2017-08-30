package lesziy.carol.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    @RequestMapping("/api")
    public Integer wotPage() {
        return 2;
    }
}
