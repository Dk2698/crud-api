package com.kumar.crudapi.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter; // Spring will inject the @Component you created

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) {
//
//        http
//                .securityMatcher(
//                        new NegatedServerWebExchangeMatcher(
//                                new OrServerWebExchangeMatcher(pathMatchers("/app/**", "/i18n/**", "/content/**", "/swagger-ui/**"))
//                        )
//                )
//                .csrf(csrf ->
//                        csrf
//                                .csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
//                                // See https://stackoverflow.com/q/74447118/65681
//                                .csrfTokenRequestHandler(new ServerCsrfTokenRequestAttributeHandler())
//                )
//                // See https://github.com/spring-projects/spring-security/issues/5766

    /// /                .addFilterAt(new CookieCsrfFilter(), SecurityWebFiltersOrder.REACTOR_CONTEXT)
    /// /                .addFilterAfter(new SpaWebFilter(), SecurityWebFiltersOrder.HTTPS_REDIRECT)
//                .headers(headers ->
//                        headers
//                                .contentSecurityPolicy(csp -> csp.policyDirectives(securityProperties.getContentSecurityPolicy()))
//                                .frameOptions(frameOptions -> frameOptions.mode(XFrameOptionsServerHttpHeadersWriter.Mode.DENY))
//                                .referrerPolicy(referrer ->
//                                        referrer.policy(ReferrerPolicyServerHttpHeadersWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
//                                )
//                                .permissionsPolicy(permissions ->
//                                        permissions.policy(
//                                                "camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()"
//                                        )
//                                )
//                )
//                .authorizeExchange(authz ->
//                                // prettier-ignore
//                                authz
//                                        .pathMatchers("/")
//                                        .permitAll()
//                                        .pathMatchers("/*.*")
//                                        .permitAll()
//                                        .pathMatchers("/api/authenticate")
//                                        .permitAll()
//                                        .pathMatchers("/api/auth-info")
//                                        .permitAll()
//                                        .pathMatchers("/api/admin/**")
//                                        .hasAuthority(SecurityConstants.ADMIN)
//                                        .pathMatchers("/api/**")
//                                        .authenticated()
//                                        .pathMatchers("/masters/**")
//                                        .authenticated()
//                                        // microfrontend resources are loaded by webpack without authentication, they need to be public
//                                        .pathMatchers("/services/*/*.js")
//                                        .permitAll()
//                                        .pathMatchers("/services/*/*.js.map")
//                                        .permitAll()
//                                        .pathMatchers("/services/*/management/health/readiness")
//                                        .permitAll()
//                                        .pathMatchers("/services/*/v3/api-docs")
//                                        .hasAuthority(SecurityConstants.ADMIN)
//                                        .pathMatchers("/services/**")
//                                        .authenticated()
//                                        .pathMatchers("/v3/api-docs/**")
//                                        .hasAuthority(SecurityConstants.ADMIN)
//                                        .pathMatchers("/management/health")
//                                        .permitAll()
//                                        .pathMatchers("/management/health/**")
//                                        .permitAll()
//                                        .pathMatchers("/management/info")
//                                        .permitAll()
//                                        .pathMatchers("/management/prometheus")
//                                        .permitAll()
//                                        .pathMatchers("/management/**")
//                                        .permitAll()
//                        //.pathMatchers("/management/**").hasAuthority(SecurityConstants.ADMIN)
//                )
//
//        http
//                // ...
//                .httpBasic(withDefaults());
//        return http.build();
//    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //.csrf(Customizer.withDefaults())
        // .httpBasic(Customizer.withDefaults())
        // .formLogin(Customizer.withDefaults())
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
//                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**", "/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                // Register your JWT filter before the standard login filter
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }
}
