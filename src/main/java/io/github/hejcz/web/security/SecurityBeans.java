package io.github.hejcz.web.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;

@Configuration
public class SecurityBeans {

    @Bean
    DaoAuthenticationProvider authenticationProvider(UserFacadeDetailsService userFacadeDetailsService) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userFacadeDetailsService);
        authenticationProvider.setPasswordEncoder(encoder());
        return authenticationProvider;
    }

    @Bean
    CsrfTokenRepository csrfTokenRepository() {
        final CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        csrfTokenRepository.setSecure(true);
        return csrfTokenRepository;
    }

    @Bean
    PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

}
