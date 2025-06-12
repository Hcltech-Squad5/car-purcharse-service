package com.hcltech.car_purcharse_service.config;

import com.hcltech.car_purcharse_service.jwt.JwtFilter;
import com.hcltech.car_purcharse_service.jwt.MyUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Uncomment this if you plan to use @PreAuthorize, @PostAuthorize etc.
public class SecurityConfig {

    private static final String[] SWAGGER_WHITE_LIST = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/swagger-resources"
    }; // Added /swagger-resources without ** for completeness, though ** usually covers it.

    private static final String[] AUTHENTICATION_WHITE_LIST = {

            "/v1/api/admins/**",
            "/v1/api/auth/**",
            "/v1/api/buyers/create",
            "/v1/api/sellers/create"

    };

    private MyUserDetailsService myUserDetailsService;
    private JwtFilter jwtFilter;

    public SecurityConfig(MyUserDetailsService myUserDetailsService, JwtFilter jwtFilter) {
        this.myUserDetailsService = myUserDetailsService;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless APIs
                .cors(cors -> cors.disable()) // Adjust CORS as needed for your frontend
                .headers(header -> header.frameOptions(frameOptionsConfig -> frameOptionsConfig.disable())) // For H2-Console if you use it, or iframes in general

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Crucial for JWTs

                .authorizeHttpRequests(request -> request
                        // Explicitly permit authentication endpoints
                        .requestMatchers(AUTHENTICATION_WHITE_LIST).permitAll()
                        // ALL OTHER REQUESTS require authentication
                        .anyRequest().authenticated()
                )
                // Remove httpBasic and formLogin if you are solely relying on JWT
                // .httpBasic(Customizer.withDefaults())
                // .formLogin(Customizer.withDefaults())

                .authenticationProvider(authenticationProvider())
                // Add your custom JWT filter before Spring Security's UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(myUserDetailsService); // Set UserDetailsService here
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // These paths will completely bypass the Spring Security filter chain
        return (web -> web.ignoring().requestMatchers(SWAGGER_WHITE_LIST));
    }
}