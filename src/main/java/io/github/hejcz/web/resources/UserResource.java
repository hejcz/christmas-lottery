package io.github.hejcz.web.resources;

import io.github.hejcz.domain.user.DtoUser;
import io.github.hejcz.domain.user.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.stream.Stream;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {
}

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserResource {

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

    @GetMapping("no-one")
    DtoUser noUser() {
        return new DtoUser();
    }

    @GetMapping("java12")
    long java12() {
        return Stream.ofNullable(null).count();
    }

}