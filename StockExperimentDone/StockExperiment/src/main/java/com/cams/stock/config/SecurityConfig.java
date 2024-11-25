package com.cams.stock.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.cams.stock.service.CustomUserDetailsService;
@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Autowired
	private CustomUserDetailsService userDetailsService;

    @SuppressWarnings({ "deprecation", "removal" })
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(authorizeRequests ->
                authorizeRequests
                    .requestMatchers("/", "/login", "/signup", "/adminPage", "/uploadStockList").permitAll() // Public access (No need for this??)
                    .requestMatchers("/h2-console/**").permitAll() // Allow access to H2 console (No need for this??)
//                    .requestMatchers("/adminPage", "/uploadStockList").hasRole("ADMIN") // Restrict to admin role
            )
            .formLogin(formLogin ->
            formLogin
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
	        )
	        .logout(logout ->
	            logout
	                .logoutUrl("/logout")
	                .logoutSuccessUrl("/")
	                .invalidateHttpSession(true)
	                .deleteCookies("JSESSIONID")
	                .permitAll()
	        )
	        .sessionManagement(sessionManagement ->
	            sessionManagement
	                .maximumSessions(1)
	                .maxSessionsPreventsLogin(true)
	        )
	        .csrf(csrf -> csrf
	            .ignoringRequestMatchers("/h2-console/**", "/login/**", "/signup/**", "/uploadStockList/**")
	        )
            .headers(headers -> headers
                .frameOptions().sameOrigin() // Allow frames for H2 console
            );
        return http.build();
    }
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    	auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}