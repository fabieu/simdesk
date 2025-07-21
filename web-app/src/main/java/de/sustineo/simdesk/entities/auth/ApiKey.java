package de.sustineo.simdesk.entities.auth;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Collections;
import java.util.List;

@Data
public class ApiKey {
    public static final String HEADER_NAME = "X-API-KEY";

    private Integer id;
    private Integer userId;
    private String apiKey;
    private String name;
    private Boolean active;
    private transient List<UserRoleEnum> roles;

    public List<GrantedAuthority> getGrantedAuthorities() {
        if (roles == null) {
            return Collections.emptyList();
        }

        List<String> authorities = roles.stream()
                .map(Enum::name)
                .toList();
        return AuthorityUtils.createAuthorityList(authorities);
    }
}
