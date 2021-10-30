package io.github.hejcz.web.security;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserFacadeDetailsService userFacadeDetailsService;

    public SecurityConfig(UserFacadeDetailsService userFacadeDetailsService) {
        this.userFacadeDetailsService = userFacadeDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .and()
                .httpBasic()
                .authenticationEntryPoint(new NoWwwAuthenticateEntryPoint())
                .and()
                .authenticationProvider(authenticationProvider())
                .csrf()
                .disable();
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userFacadeDetailsService);
        authenticationProvider.setPasswordEncoder(encoder());
        return authenticationProvider;
    }

    @Bean
    PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    private static class NoWwwAuthenticateEntryPoint extends BasicAuthenticationEntryPoint {

        public NoWwwAuthenticateEntryPoint() {
            setRealmName("santa-realm");
        }

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
        }
    }
}
