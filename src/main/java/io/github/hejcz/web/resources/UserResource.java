package io.github.hejcz.web.resources;

import io.github.hejcz.domain.user.SystemRole;
import io.github.hejcz.domain.user.UserFacade;
import io.github.hejcz.domain.user.UserSecurityFacade;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
class UserResource {

    private final UserFacade userFacade;
    private final UserSecurityFacade userSecurityFacade;
    private final HttpServletRequest httpRequest;

    public UserResource(UserFacade userFacade, UserSecurityFacade userSecurityFacade, HttpServletRequest httpRequest) {
        this.userFacade = userFacade;
        this.userSecurityFacade = userSecurityFacade;
        this.httpRequest = httpRequest;
    }

    @GetMapping("api/ids")
    @Secured("ADMIN")
    Collection<User> allUsers() {
        return userFacade.findRegularUsers()
                .stream()
                .map(user -> new User(user.id(), user.name(), user.surname()))
                .sorted(Comparator.comparingInt(User::id))
                .collect(Collectors.toList());
    }

    @GetMapping("api/ids/current/roles")
    @Secured({"USER", "ADMIN"})
    List<SystemRole> roles() {
        return Collections.singletonList(userFacade.loggedUserOrException().systemRole());
    }

    @PutMapping("api/passwords/recovery")
    void initPasswordReset(@RequestBody String email) {
        userSecurityFacade.requestPasswordRecovery(email);
    }

    @PutMapping("api/passwords")
    void resetPassword(@RequestBody NewPassword newPassword) {
        userSecurityFacade.changePassword(newPassword.token(), newPassword.newPassword());
    }

}
