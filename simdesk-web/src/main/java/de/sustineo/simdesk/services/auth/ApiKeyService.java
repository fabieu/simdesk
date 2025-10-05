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

        ApiKey fullApiKey = apiKeyMapper.findActiveByApiKey(apiKey);
        if (fullApiKey == null) {
            return Optional.empty();
        }

        fullApiKey.setRoles(userPermissionMapper.findRolesByUserId(fullApiKey.getUserId()));

        return Optional.of(fullApiKey);
    }

    public void create(Integer userId, String name) {
        String apiKey = idGenerator.generateRandomString(32);

        apiKeyMapper.insert(userId, apiKey, name);
    }

    @CacheEvict(cacheNames = CacheNames.API_KEY, key = "#fullApiKey.apiKey")
    public void deleteApiKey(ApiKey fullApiKey) {
        apiKeyMapper.deleteById(fullApiKey);
    }
}
