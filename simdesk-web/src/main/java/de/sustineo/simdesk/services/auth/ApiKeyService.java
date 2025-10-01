package de.sustineo.simdesk.services.auth;

import de.sustineo.simdesk.configuration.CacheNames;
import de.sustineo.simdesk.entities.auth.ApiKey;
import de.sustineo.simdesk.mybatis.mapper.UserApiKeyMapper;
import de.sustineo.simdesk.mybatis.mapper.UserPermissionMapper;
import de.sustineo.simdesk.services.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApiKeyService {
    private final UserApiKeyMapper apiKeyMapper;
    private final UserPermissionMapper userPermissionMapper;
    private final IdGenerator idGenerator;

    public List<ApiKey> getByUserId(Integer userId) {
        return apiKeyMapper.findByUserId(userId);
    }

    @Cacheable(cacheNames = CacheNames.API_KEY, key = "#apiKey")
    public Optional<ApiKey> getActiveByApiKey(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            return Optional.empty();
        }

        ApiKey detailedApiKey = apiKeyMapper.findActiveByApiKey(apiKey);
        if (detailedApiKey == null) {
            return Optional.empty();
        }

        detailedApiKey.setRoles(userPermissionMapper.findRolesByUserId(detailedApiKey.getUserId()));

        return Optional.of(detailedApiKey);
    }

    public void create(Integer userId, String name) {
        apiKeyMapper.insert(userId, generateKey(), name);
    }

    @CacheEvict(cacheNames = CacheNames.API_KEY, key = "#detailedApiKey.apiKey")
    public void deleteApiKey(ApiKey detailedApiKey) {
        apiKeyMapper.deleteById(detailedApiKey);
    }

    private String generateKey() {
        return idGenerator.generateRandomString(32);
    }
}
