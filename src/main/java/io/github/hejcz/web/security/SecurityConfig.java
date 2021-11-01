package io.github.hejcz.web.security;

import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserFacadeDetailsService userFacadeDetailsService;
    private final Environment environment;
    private final CsrfTokenRepository csrfTokenRepository;

    public SecurityConfig(UserFacadeDetailsService userFacadeDetailsService, Environment environment,
                          CsrfTokenRepository csrfTokenRepository) {
        this.userFacadeDetailsService = userFacadeDetailsService;
        this.environment = environment;
        this.csrfTokenRepository = csrfTokenRepository;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if (Arrays.stream(environment.getActiveProfiles()).noneMatch(p -> p.equals("dev"))) {
            http = http.requiresChannel().anyRequest().requiresSecure().and();
        }

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .and()
                .httpBasic()
                .authenticationEntryPoint(new NoWwwAuthenticateEntryPoint())
                .and()
                .authenticationProvider(authenticationProvider())
                .csrf()
                .csrfTokenRepository(csrfTokenRepository)
                .and()
                .exceptionHandling()
                .accessDeniedHandler((request, response, accessDeniedException) ->
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN));
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider() {
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
