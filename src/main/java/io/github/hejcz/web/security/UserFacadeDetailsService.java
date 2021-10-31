package io.github.hejcz.web.security;

import io.github.hejcz.domain.user.DtoUser;
import io.github.hejcz.domain.user.UserFacadeImpl;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
class UserFacadeDetailsService implements UserDetailsService {

    private final UserFacadeImpl userFacade;

    public UserFacadeDetailsService(UserFacadeImpl userFacade) {
        this.userFacade = userFacade;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userFacade.findByLogin(username)
                .map(this::dtoToUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException(""));
    }

    private UserDetails dtoToUserDetails(DtoUser user) {
        return new User(
                user.login(),
                user.password(),
                Collections.singletonList(new SimpleGrantedAuthority(user.isAdmin() ? "ADMIN" : "USER"))
        );
    }

}
