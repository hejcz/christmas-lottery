package io.github.hejcz.web.resources;

import io.github.hejcz.domain.user.DtoUser;
import io.github.hejcz.domain.user.SystemRole;
import io.github.hejcz.domain.user.UserFacade;
import io.github.hejcz.domain.user.UserSecurityFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
class UserResource {

    private final UserFacade userFacade;
    private final UserSecurityFacade userSecurityFacade;
    private final HttpServletRequest httpRequest;

    @GetMapping("api/users")
    @Secured("ADMIN")
    Collection<User> allUsers() {
        return userFacade.findRegularUsers()
            .stream()
            .map(user -> new User(user.id(), user.name(), user.surname()))
            .sorted(Comparator.comparingInt(User::getId))
            .collect(Collectors.toList());
    }

    @GetMapping("api/users/current/roles")
    @Secured({"USER", "ADMIN"})
    List<SystemRole> roles() {
        return Collections.singletonList(userFacade.loggedUserOrException().systemRole());
    }

    @PutMapping("api/passwords/recovery")
    void initPasswordReset(@RequestBody String email) {
        userSecurityFacade.requestPasswordRecovery(httpRequest.getRequestURL().toString(), email);
    }

}
