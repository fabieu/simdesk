package de.sustineo.simdesk.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Weigher;
import org.jspecify.annotations.NullMarked;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;

@Configuration
public class CacheConfiguration {
    private static final int MAXIMUM_WEIGHT = 50_000;

    @Bean
    CaffeineCacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(CacheNames.ALL);

        // Define the global builder (applies to all caches unless overridden)
        cacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .maximumWeight(MAXIMUM_WEIGHT)
                        .weigher(BoundedSizeWeigher.INSTANCE)
                        .expireAfterWrite(Duration.ofMinutes(15))
                        .recordStats()
        );

        return cacheManager;
    }

    @NullMarked
    private enum BoundedSizeWeigher implements Weigher<Object, Object> {
        INSTANCE;

        @Override
        public int weigh(Object key, Object value) {
            int weight = switch (value) {
                case Collection<?> collection -> Math.max(1, collection.size());
                case Map<?, ?> map -> Math.max(1, map.size());
                default -> 1;
            };

            return Math.min(weight, MAXIMUM_WEIGHT / 10);
        }
    }
}
