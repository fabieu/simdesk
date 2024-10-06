package de.sustineo.simdesk.configuration;

import com.vaadin.flow.spring.security.NavigationAccessControlConfigurer;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import de.sustineo.simdesk.services.auth.UserService;
import de.sustineo.simdesk.services.discord.DiscordService;
import de.sustineo.simdesk.views.LoginView;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
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

import java.util.*;
import java.util.stream.Collectors;

@Log
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration extends VaadinWebSecurity {
    private final String[] PUBLIC_PATHS = {
            "/public/**",
            "/assets/**",
            "/icons/**",
    };
    private static final String LOGIN_URL = "/login";
    private static final String LOGIN_SUCCESS_URL = "/";

    public static final String ATTRIBUTE_AUTH_PROVIDER = "auth_provider";
    public static final String AUTH_PROVIDER_DISCORD = "discord";
    public static final String AUTH_PROVIDER_DATABASE = "database";

    public static final Long USER_ID_ADMIN = 10000L;

    private final Optional<DiscordService> discordService;
    private final UserService userService;

    public SecurityConfiguration(Optional<DiscordService> discordService,
                                 UserService userService) {
        this.discordService = discordService;
        this.userService = userService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Delegating the responsibility of general configurations
        // of http security to the super class. It's configuring
        // the followings: Vaadin's CSRF protection by ignoring
        // framework's internal requests, default request cache,
        // ignoring public views annotated with @AnonymousAllowed,
        // restricting access to other views/endpoints, and enabling
        // NavigationAccessControl authorization.

        // Configure your static resources with public access before calling
        // super.configure(HttpSecurity) as it adds final anyRequest matcher
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(antMatchers(PUBLIC_PATHS)).permitAll()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage(LOGIN_URL).permitAll()
                        .loginProcessingUrl(LOGIN_URL)
                        .defaultSuccessUrl(LOGIN_SUCCESS_URL)
                );

        if (ProfileManager.isOAuth2ProfileEnabled()) {
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
        super.configure(http);

        // This is important to register your login view to the
        // navigation access control mechanism:
        setLoginView(http, LoginView.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }

    @Bean
    public static NavigationAccessControlConfigurer navigationAccessControlConfigurer() {
        return new NavigationAccessControlConfigurer()
                .withAnnotatedViewAccessChecker();
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();

        return (request -> {
            // Delegate to the default implementation for loading a user
            OAuth2User user = delegate.loadUser(request);

            if (AUTH_PROVIDER_DISCORD.equals(request.getClientRegistration().getRegistrationId()) && discordService.isPresent()) {
                try {
                    String accessToken = request.getAccessToken().getTokenValue();

                    // Get the roles of the user from the Discord bot installed in the guild
                    Set<Long> memberRoleIds = discordService.get().getMemberRoleIds(accessToken);

                    // Map the guild roles to Spring Security authorities
                    Set<GrantedAuthority> authorities = userService.getAllRoles().stream()
                            .filter(userRole -> userRole.getDiscordRoleId() != null)
                            .filter(userRole -> memberRoleIds.contains(userRole.getDiscordRoleId()))
                            .map(userRole -> new SimpleGrantedAuthority(userRole.getName()))
                            .collect(Collectors.toSet());

                    Map<String, Object> attributes = new LinkedHashMap<>(user.getAttributes());
                    attributes.put(ATTRIBUTE_AUTH_PROVIDER, AUTH_PROVIDER_DISCORD);

                    return new DefaultOAuth2User(authorities, Collections.unmodifiableMap(attributes), request.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName());
                } catch (Exception e) {
                    log.severe(String.format("Failed to load roles for user: %s, reason: %s", user.getName(), e.getMessage()));
                    throw new OAuth2AuthenticationException(new OAuth2Error("invalid_token", e.getMessage(), ""));
                }
            }

            return user;
        });
    }
}