package jp.co.axa.apidemo.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 *  ehcache config class that enables the caching by @EnableCaching
 */
@Configuration
@EnableCaching
public class CacheConfig {
}
