package de.sustineo.simdesk.services;

import de.sustineo.simdesk.configuration.CacheNames;
import de.sustineo.simdesk.entities.Setting;
import de.sustineo.simdesk.mybatis.mapper.SettingMapper;
import de.sustineo.simdesk.utils.json.JsonClient;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SettingService {
    private final SettingMapper settingMapper;

    @Cacheable(cacheNames = CacheNames.SETTINGS, key = "#key")
    public String get(String key) {
        Setting property = settingMapper.findActive(key);
        if (property == null) {
            return null;
        }

        return property.getValue();
    }

    @Cacheable(cacheNames = CacheNames.SETTINGS, key = "#key")
    public <T> T getJson(String key, Class<T> clazz) {
        String value = get(key);
        if (value == null) {
            return null;
        }

        return JsonClient.fromJson(value, clazz);
    }

    @CacheEvict(cacheNames = CacheNames.SETTINGS, key = "#key")
    public void set(String key, String value) {
        settingMapper.update(key, value);
    }

    @CacheEvict(cacheNames = CacheNames.SETTINGS, key = "#key")
    public void setJson(String key, Object value) {
        set(key, JsonClient.toJson(value));
    }
}
