package de.sustineo.simdesk.services.auth;

import de.sustineo.simdesk.entities.auth.ApiKey;
import de.sustineo.simdesk.mapper.UserApiKeyMapper;
import de.sustineo.simdesk.mapper.UserPermissionMapper;
import de.sustineo.simdesk.services.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApiKeyService {
    private static final String CACHE_ACTIVE_API_KEYS = "activeApiKeys";

    private final UserApiKeyMapper apiKeyMapper;
    private final UserPermissionMapper userPermissionMapper;
    private final CacheManager cacheManager;
    private final IdGenerator idGenerator;

    public List<ApiKey> getByUserId(Integer userId) {
        return apiKeyMapper.findByUserId(userId);
    }

    @Cacheable(cacheNames = CACHE_ACTIVE_API_KEYS)
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
        apiKeyMapper.insert(userId, generateKey(), name);
    }

    public void deleteApiKey(ApiKey apiKey) {
        removeActiveApiKeyFromCache(apiKey);
        apiKeyMapper.deleteById(apiKey);
    }

    public void removeActiveApiKeysFromCache(Integer userId) {
        getByUserId(userId).forEach(this::removeActiveApiKeyFromCache);
    }

    private void removeActiveApiKeyFromCache(ApiKey apiKey) {
        Cache cache = cacheManager.getCache(CACHE_ACTIVE_API_KEYS);
        if (cache == null) {
            return;
        }

        cache.evict(apiKey.getApiKey());
    }

    private String generateKey() {
        return idGenerator.generateRandomString(32);
    }
}
