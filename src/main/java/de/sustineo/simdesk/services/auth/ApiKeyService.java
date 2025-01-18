package de.sustineo.simdesk.services.auth;

import de.sustineo.simdesk.entities.auth.ApiKey;
import de.sustineo.simdesk.mapper.UserApiKeyMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ApiKeyService {
    private final UserApiKeyMapper apiKeyMapper;

    public ApiKeyService(UserApiKeyMapper apiKeyMapper) {
        this.apiKeyMapper = apiKeyMapper;
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

        return Optional.ofNullable(apiKey);
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
