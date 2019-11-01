package io.github.hejcz.web.resources;

import io.github.hejcz.domain.user.DtoUser;
import io.github.hejcz.domain.user.UserFacade;
import io.github.hejcz.domain.user.UserSecurityFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
class UserResource {

    private final UserFacade userFacade;
    private final UserSecurityFacade userSecurityFacade;
    private final HttpServletRequest httpRequest;

    @GetMapping("users")
    @Secured("ADMIN")
    Collection<DtoUser> allUsers() {
        return userFacade.findRegularUsers();
    }

    @GetMapping("users/current")
    @Secured("USER")
    DtoUser singleUser() {
        return userFacade.loggedUserOrException();
    }

    @PutMapping("passwords/recovery")
    void initPasswordReset(@RequestBody String email) {
        userSecurityFacade.requestPasswordRecovery(httpRequest.getRequestURL().toString(), email);
    }

}
