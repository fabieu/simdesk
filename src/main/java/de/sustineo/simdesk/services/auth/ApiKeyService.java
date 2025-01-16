package de.sustineo.simdesk.services.auth;

import de.sustineo.simdesk.entities.auth.ApiKey;
import de.sustineo.simdesk.mapper.UserApiKeyMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ApiKeyService {
    private final UserApiKeyMapper apiKeyMapper;

    public ApiKeyService(UserApiKeyMapper apiKeyMapper) {
        this.apiKeyMapper = apiKeyMapper;
    }

    @Cacheable("apiKey")
    public Optional<ApiKey> getValid(String apiKeyString) {
        if (apiKeyString == null) {
            return Optional.empty();
        }

        ApiKey apiKey = apiKeyMapper.findByApiKey(apiKeyString);
        if (apiKey != null && Boolean.TRUE.equals(apiKey.getActive())) {
            return Optional.of(apiKey);
        }

        return Optional.empty();
    }

    @CacheEvict("apiKey")
    public void deleteApiKey(ApiKey apiKey) {
        apiKeyMapper.deleteById(apiKey);
    }

    @CacheEvict("apiKey")
    public void updateStatus(ApiKey apiKey) {
        apiKeyMapper.updateStatus(apiKey);
    }
}
