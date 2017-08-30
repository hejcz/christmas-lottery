package lesziy.carol.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletResponse;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/api/login").permitAll()
                .antMatchers("/api/**").hasRole("USER")
                .antMatchers("/**").permitAll()
            .and()
                .formLogin()
                    .loginProcessingUrl("/api/login")
                    .successHandler(authenticationSuccessHandler())
                    .failureHandler(authenticationFailureHandler())
            .and()
                .exceptionHandling()
                    .authenticationEntryPoint(authenticationEntryPoint())
            .and()
            .csrf()
                .disable();
    }

    private AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) ->
            response.setStatus(HttpServletResponse.SC_OK);
    }

    private AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) ->
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    /**
     * W przypadku nieautoryzowanego dostępu zamiast przekierowywać na loginPage
     * po prostu ustawia status.
     */
    private AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            if (authException != null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        };
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user").password("password").roles("USER");
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.debug(true);
    }
}
