package io.github.hejcz.web.security;

import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CsrfTokenRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            DaoAuthenticationProvider daoAuthenticationProvider, Environment environment,
            CsrfTokenRepository csrfTokenRepository) throws Exception {
        if (Arrays.stream(environment.getActiveProfiles()).noneMatch(p -> p.equals("dev"))) {
            http = http.requiresChannel().anyRequest().requiresSecure().and();
        }

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .and()
                .httpBasic()
                .authenticationEntryPoint(new NoWwwAuthenticateEntryPoint())
                .and()
                .authenticationProvider(daoAuthenticationProvider)
                .csrf()
                .csrfTokenRepository(csrfTokenRepository);

        return http.build();
    }

    private static class NoWwwAuthenticateEntryPoint extends BasicAuthenticationEntryPoint {

        public NoWwwAuthenticateEntryPoint() {
            setRealmName("santa-realm");
        }

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response,
                AuthenticationException authException) throws IOException {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
        }
    }
}
