package io.github.hejcz.web.resources;

import io.github.hejcz.domain.user.DtoUser;
import io.github.hejcz.domain.user.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.stream.Stream;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
class UserResource {

    private final UserFacade userFacade;

    @GetMapping("admin")
    @Secured("ADMIN")
    Collection<DtoUser> allUsers() {
        return userFacade.loadUsers();
    }

    @GetMapping("user")
    @Secured("USER")
    DtoUser singleUser() {
        return userFacade.loggedUserOrException();
    }

    @GetMapping("java12")
    long java12() {
        return Stream.ofNullable(null).count();
    }

}