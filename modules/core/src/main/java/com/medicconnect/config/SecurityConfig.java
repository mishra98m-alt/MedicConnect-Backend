package com.medicconnect.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Value("${app.cors.allowed-origin:*}")
    private String allowedOrigin;

    @Autowired(required = false)
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // Public routes (NO JWT REQUIRED)
    private static final String[] PUBLIC_ENDPOINTS = {
            "/api/**",
            "/openmrs/**",
            "/actuator/health",
            "/actuator/info",
            "/api/v1/ping",
            "/error"
    };

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // ---- CORS ----
        http.cors(cors -> cors.configurationSource(request -> {
            CorsConfiguration config = new CorsConfiguration();

            if ("*".equals(allowedOrigin)) {
                config.addAllowedOriginPattern("*");
            } else {
                config.setAllowedOrigins(List.of(allowedOrigin));
            }

            config.addAllowedHeader("*");
            config.addAllowedMethod("*");
            config.setAllowCredentials(true);
            return config;
        }));

        // ---- Disable things we don't need ----
        http.csrf(csrf -> csrf.disable());
        http.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // ---- Security Rules ----
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()  // EVERYTHING PUBLIC
                .anyRequest().permitAll()                       // NOTHING REQUIRES JWT
        );

        // ---- Error Handling ----
        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .accessDeniedHandler(new CustomAccessDeniedHandler())
        );

        // ---- Disable these ----
        http.formLogin(login -> login.disable());
        http.httpBasic(basic -> basic.disable());

        // ---- Optional JWT filter (only sets authentication if token sent) ----
        if (jwtAuthenticationFilter != null) {
            http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        }

        return http.build();
    }

    // ---- CORS FILTER ----
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        if ("*".equals(allowedOrigin)) {
            config.addAllowedOriginPattern("*");
        } else {
            config.setAllowedOrigins(List.of(allowedOrigin));
        }

        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
