package de.sustineo.simdesk.services;

import de.sustineo.simdesk.entities.DynamicProperty;
import de.sustineo.simdesk.entities.mapper.PropertyMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class PropertyService {
    private final PropertyMapper propertyMapper;

    public PropertyService(PropertyMapper propertyMapper) {
        this.propertyMapper = propertyMapper;
    }

    @Cacheable(value = "properties", key = "#key")
    public String getPropertyValue(String key) {
        DynamicProperty property = propertyMapper.findByKey(key);
        if (property == null) {
            return null;
        }

        return property.getValue();
    }

    @CacheEvict(value = "properties", key = "#key")
    public void setPropertyValue(String key, String value) {
        propertyMapper.update(key, value);
    }
}
