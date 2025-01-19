package de.sustineo.simdesk.services.auth;

import de.sustineo.simdesk.entities.auth.ApiKey;
import de.sustineo.simdesk.mapper.UserApiKeyMapper;
import de.sustineo.simdesk.mapper.UserPermissionMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ApiKeyService {
    private final UserApiKeyMapper apiKeyMapper;
    private final UserPermissionMapper userPermissionMapper;

    public ApiKeyService(UserApiKeyMapper apiKeyMapper,
                         UserPermissionMapper userPermissionMapper) {
        this.apiKeyMapper = apiKeyMapper;
        this.userPermissionMapper = userPermissionMapper;
    }

    public List<ApiKey> getByUserId(Integer userId) {
        return apiKeyMapper.findByUserId(userId);
    }

    @Cacheable(cacheNames = "activeApiKeys")
    public Optional<ApiKey> getActiveByApiKey(String apiKeyString) {
        if (apiKeyString == null) {
            return Optional.empty();
        }

        ApiKey apiKey = apiKeyMapper.findActiveByApiKey(apiKeyString);
        if (apiKey == null) {
            return Optional.empty();
        }

        apiKey.setRoles(userPermissionMapper.findRolesByUserId(apiKey.getUserId()));

        return Optional.of(apiKey);
    }

    public void create(Integer userId, String name) {
        apiKeyMapper.insert(userId, generateApiKey(), name);
    }

    @CacheEvict(cacheNames = "activeApiKeys", key = "#apiKey.apiKey")
    public void deleteApiKey(ApiKey apiKey) {
        apiKeyMapper.deleteById(apiKey);
    }

    @CacheEvict(cacheNames = "activeApiKeys", key = "#apiKey.apiKey")
    public void updateStatus(ApiKey apiKey) {
        apiKeyMapper.updateStatus(apiKey);
    }

    private String generateApiKey() {
        return UUID.randomUUID().toString();
    }
}
