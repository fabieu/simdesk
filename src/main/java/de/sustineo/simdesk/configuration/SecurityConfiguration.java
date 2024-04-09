package de.sustineo.simdesk.configuration;

import com.vaadin.flow.spring.security.NavigationAccessControlConfigurer;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import de.sustineo.simdesk.entities.json.discord.Guild;
import de.sustineo.simdesk.services.DiscordService;
import de.sustineo.simdesk.views.LoginView;
import discord4j.discordjson.json.RoleData;
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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Log
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration extends VaadinWebSecurity {
    private final String[] PUBLIC_PATHS = {
            "/public/**",
            "/assets/**",
    };
    private final String OAUTH2_PROVIDER_DISCORD = "discord";
    private final String DISCORD_ROLE_PREFIX = "AST-";

    private final Optional<DiscordService> discordService;

    public SecurityConfiguration(Optional<DiscordService> discordService) {
        this.discordService = discordService;
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
                );

        if (ProfileManager.isOAuth2ProfileEnabled()) {
            http
                    .oauth2Login(oauth2 -> oauth2
                            .loginPage("/login/oauth")
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

            if (OAUTH2_PROVIDER_DISCORD.equals(request.getClientRegistration().getRegistrationId()) && discordService.isPresent()) {
                Optional<Long> userId = Optional.ofNullable(user.getAttribute("id")).map(o -> Long.parseLong((String) o));
                if (userId.isEmpty()) {
                    log.severe("Failed to find id attribute for user: " + user.getName());
                    return user;
                }

                try {
                    String guildId = discordService.get().getGuildId();

                    // Check if the user is a member of the provided guild
                    List<Guild> userGuilds = discordService.get().getGuildsOfCurrentUser(request.getAccessToken().getTokenValue());
                    if (userGuilds.stream().noneMatch(guild -> guildId.equals(guild.getId()))) {
                        log.warning(String.format("OAuth2 failed - User %s is not a member of the provided guild", user.getName()));
                        throw new OAuth2AuthenticationException(new OAuth2Error("invalid_user", "User is not a member of the required guild", ""));
                    }

                    // Get the roles of the user from the Discord bot installed in the guild
                    List<RoleData> userRoles = discordService.get().getRolesOfMember(userId.get());

                    // Map the guild roles to Spring Security authorities
                    Set<GrantedAuthority> authorities = userRoles.stream()
                            .filter(role -> role.name().startsWith(DISCORD_ROLE_PREFIX))
                            .map(role -> new SimpleGrantedAuthority(role.name().toUpperCase()))
                            .collect(Collectors.toSet());

                    return new DefaultOAuth2User(authorities, user.getAttributes(), request.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName());
                } catch (Exception e) {
                    log.severe(String.format("Failed to load roles for user: %s, reason: %s", user.getName(), e.getMessage()));
                    throw new OAuth2AuthenticationException(new OAuth2Error("invalid_token", e.getMessage(), ""));
                }
            }

            return user;
        });
    }
}