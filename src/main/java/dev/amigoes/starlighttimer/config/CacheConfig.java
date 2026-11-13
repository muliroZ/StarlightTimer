package dev.amigoes.starlighttimer.config;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.amigoes.starlighttimer.domain.progression.entities.Constelacao;
import dev.amigoes.starlighttimer.domain.progression.entities.Insignia;
import dev.amigoes.starlighttimer.domain.progression.entities.LimiteNivel;
import dev.amigoes.starlighttimer.domain.progression.entities.Patente;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.List;
import java.util.Map;


@Configuration
public class CacheConfig implements CachingConfigurer {
    private static final Logger log = LoggerFactory.getLogger(CacheConfig.class);

    public static final String CACHE_LIMITES_NIVEL = "catalogo-limites-nivel";
    public static final String CACHE_PATENTES = "catalogo-patentes";
    public static final String CACHE_INSIGNIAS = "catalogo-insignias";
    public static final String CACHE_CONSTELACOES = "catalogo-constelacoes";

    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                log.warn("Falha ao consultar cache '{}' (chave '{}'): {}",
                        cache.getName(), key, exception.getMessage());
            }
            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                log.warn("Falha ao gravar no cache '{}' (chave '{}'): {}",
                        cache.getName(), key, exception.getMessage());
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                log.warn("Falha ao remover entrada do cache '{}' (chave '{}'): {}",
                        cache.getName(), key, exception.getMessage());
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                log.warn("Falha ao limpar cache '{}': {}", cache.getName(), exception.getMessage());
            }
        };
    }
    private <T> RedisSerializationContext.SerializationPair<Object> serializadorLista(
            ObjectMapper objectMapper,
            Class<T> classeItem
    ) {
        JavaType tipoLista = objectMapper.getTypeFactory().constructCollectionType(List.class, classeItem);

        RedisSerializer<Object> serializer = new RedisSerializer<>() {
            @Override
            public byte[] serialize(Object value) throws SerializationException {
                if (value == null) {
                    return new byte[0];
                }
                try {
                    return objectMapper.writeValueAsBytes(value);
                } catch (Exception e) {
                    throw new SerializationException("Erro ao serializar lista de " + classeItem.getSimpleName(), e);
                }
            }

            @Override
            public Object deserialize(byte[] bytes) throws SerializationException {
                if (bytes == null || bytes.length == 0) {
                    return null;
                }
                try {
                    return objectMapper.readValue(bytes, tipoLista);
                } catch (Exception e) {
                    throw new SerializationException("Erro ao desserializar lista de " + classeItem.getSimpleName(), e);
                }
            }
        };

        return RedisSerializationContext.SerializationPair.fromSerializer(serializer);
    }
    @Bean
    public RedisCacheManager cacheManager(
            RedisConnectionFactory connectionFactory,
            @Value("${spring.cache.redis.time-to-live:600000}") long ttlMillis
    ) {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        Duration ttl = Duration.ofMillis(ttlMillis);
        RedisCacheConfiguration padrao = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMillis(ttlMillis))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));

        Map<String, RedisCacheConfiguration> cachesPorNome = Map.of(
                CACHE_LIMITES_NIVEL, padrao.serializeValuesWith(serializadorLista(objectMapper, LimiteNivel.class)),
                CACHE_PATENTES, padrao.serializeValuesWith(serializadorLista(objectMapper, Patente.class)),
                CACHE_INSIGNIAS, padrao.serializeValuesWith(serializadorLista(objectMapper, Insignia.class)),
                CACHE_CONSTELACOES, padrao.serializeValuesWith(serializadorLista(objectMapper, Constelacao.class))
        );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(padrao)
                .withInitialCacheConfigurations(cachesPorNome)
                .build();
    }
}
