package de.sustineo.simdesk.configuration;

import com.vaadin.flow.spring.security.NavigationAccessControlConfigurer;
import com.vaadin.flow.spring.security.VaadinAwareSecurityContextHolderStrategyConfiguration;
import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;
import de.sustineo.simdesk.entities.auth.UserRoleEnum;
import de.sustineo.simdesk.filter.ApiKeyAuthenticationFilter;
import de.sustineo.simdesk.services.auth.UserService;
import de.sustineo.simdesk.services.discord.DiscordService;
import de.sustineo.simdesk.views.LoginView;
import discord4j.common.util.Snowflake;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import java.util.*;
import java.util.stream.Collectors;

@Log
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Import(VaadinAwareSecurityContextHolderStrategyConfiguration.class)
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final String[] PUBLIC_PATHS = {
            "/actuator/health", // Actuator
            "/public/**",       // Vaadin
            "/assets/**",       // Vaadin
            "/icons/**",        // Icons
            "/swagger-ui/**",   // SpringDoc
            "/openapi/**",      // SpringDoc
            "/ws/**",           // WebSockets
            "/*.png"            // Leaflet images
    };
    private static final String LOGIN_URL = "/login";
    private static final String LOGIN_SUCCESS_URL = "/";

    public static final String ATTRIBUTE_AUTH_PROVIDER = "auth_provider";
    public static final String AUTH_PROVIDER_DISCORD = "discord";
    public static final String AUTH_PROVIDER_DATABASE = "database";

    public static final int USER_ID_ADMIN = 1;

    private final ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;
    private final Optional<DiscordService> discordService;
    private final UserService userService;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        PathPatternRequestMatcher.Builder pathPatternBuilder = PathPatternRequestMatcher.withDefaults();

        http
                .with(VaadinSecurityConfigurer.vaadin(), configurer -> {
                    configurer.loginView(LoginView.class);
                })
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_PATHS).permitAll()
                        .requestMatchers("/actuator/**").hasAuthority(UserRoleEnum.ROLE_ADMIN.name())
                )
                .addFilterAfter(apiKeyAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED), pathPatternBuilder.matcher("/api/**"))
                )
                .formLogin(formLogin -> formLogin
                        .loginPage(LOGIN_URL).permitAll()
                        .loginProcessingUrl(LOGIN_URL)
                        .defaultSuccessUrl(LOGIN_SUCCESS_URL)
                );

        if (SpringProfile.isOAuth2Enabled()) {
            http
                    .oauth2Login(oauth2 -> oauth2
                            .loginPage("/login/oauth")
                            .defaultSuccessUrl(LOGIN_SUCCESS_URL)
                            .failureUrl("/login?oauth-error")
                            .permitAll()
                            .authorizationEndpoint(authorization -> authorization
                                    .baseUri("/login/oauth2/authorization")
                            )
                    );
        }

        return http.build();
    }

    @Bean
    NavigationAccessControlConfigurer navigationAccessControlConfigurer() {
        return new NavigationAccessControlConfigurer()
                .withAnnotatedViewAccessChecker();
    }

    @Bean
    PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();

        return (request -> {
            String userNameAttributeName = request.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

            // Delegate to the default implementation for loading a user
            OAuth2User user = delegate.loadUser(request);

            if (discordService.isPresent() && AUTH_PROVIDER_DISCORD.equals(request.getClientRegistration().getRegistrationId())) {
                try {
                    String userId = user.getAttribute(userNameAttributeName);
                    if (userId == null) {
                        throw new OAuth2AuthenticationException(new OAuth2Error("invalid_token", "Missing user ID", ""));
                    }

                    // Get the roles of the user from the Discord bot installed in the guild
                    Set<String> memberRoleIds = discordService.get().getMember(Snowflake.of(userId)).getRoleIds().stream()
                            .map(Snowflake::asString)
                            .collect(Collectors.toSet());

                    // Map the guild roles to Spring Security authorities
                    Set<GrantedAuthority> authorities = userService.getAllRoles().stream()
                            .filter(userRole -> userRole.getDiscordRoleId() != null)
                            .filter(userRole -> memberRoleIds.contains(userRole.getDiscordRoleId()))
                            .map(userRole -> new SimpleGrantedAuthority(userRole.getName().name()))
                            .collect(Collectors.toSet());

                    Map<String, Object> attributes = new LinkedHashMap<>(user.getAttributes());
                    attributes.put(ATTRIBUTE_AUTH_PROVIDER, AUTH_PROVIDER_DISCORD);

                    user = new DefaultOAuth2User(authorities, Collections.unmodifiableMap(attributes), userNameAttributeName);

                    userService.insertDiscordUser(user.getName(), user.getAuthorities());
                } catch (Exception e) {
                    log.severe(String.format("Failed to load roles for user: %s, reason: %s", user.getName(), e.getMessage()));
                    throw new OAuth2AuthenticationException(new OAuth2Error("invalid_token", e.getMessage(), ""));
                }
            }

            return user;
        });
    }
}