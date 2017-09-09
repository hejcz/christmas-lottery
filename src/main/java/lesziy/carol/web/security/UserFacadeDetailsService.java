package lesziy.carol.web.security;

import lesziy.carol.domain.user.DtoUser;
import lesziy.carol.domain.user.UserFacadeImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class UserFacadeDetailsService implements UserDetailsService {

    private final UserFacadeImpl userFacade;

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
