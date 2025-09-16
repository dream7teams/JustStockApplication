package net.juststock.trading.security;

import org.springframework.boot.autoconfigure.web.client.HttpMessageConvertersRestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AdminDetailsService adminDetailsService;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, AdminDetailsService adminDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.adminDetailsService = adminDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Only needed if you use /api/admin/auth/login with DaoAuthenticationProvider
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(adminDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {}) // use bean below
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Allow CORS preflight
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Admin Auth (public)
                .requestMatchers(HttpMethod.POST, "/api/admin/auth/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/admin/auth/login").permitAll()

                // Admin Messages
                // POST must be ADMIN (this is the critical change)
                .requestMatchers(HttpMethod.POST, "/api/adminmessage/messages").permitAll()
                // GETs can stay public if you want
                .requestMatchers(HttpMethod.GET, "/api/adminmessage/messages").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/adminmessage/messages/instrument/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/adminmessage/messages/history/user/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/adminmessage/messages/history/message/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/adminmessage/messages/admin/**").permitAll()

                // User Auth (OTP) public
                .requestMatchers(HttpMethod.POST, "/api/auth/send").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/resend").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/verify").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()


                // User actions requiring auth
                .requestMatchers(HttpMethod.POST, "/api/auth/logout").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/user/messages/select").permitAll()
                .requestMatchers(HttpMethod.GET,  "/api/user/messages/history").permitAll()
                
                .requestMatchers(HttpMethod.GET,"/api/market/sensex/latest").permitAll()

                // Any other /api/admin/** require ADMIN
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // Everything else denied by default
                .anyRequest().denyAll()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
