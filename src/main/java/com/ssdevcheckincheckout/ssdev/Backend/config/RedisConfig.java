package com.ssdevcheckincheckout.ssdev.Backend.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisConfig {

    public static class RedisProps {
        @Value("${spring.redis.host:localhost}")
        private String host;

        @Value("${spring.redis.port:6379}")
        private int port;

        // milliseconds
        @Value("${spring.redis.timeout:60000}")
        private long timeoutMs;

        @Value("${spring.redis.password:}")
        private String password;

        @Value("${spring.redis.database:#{null}}")
        private Integer database;

        public String getHost() { return host; }
        public int getPort() { return port; }
        public long getTimeoutMs() { return timeoutMs; }
        public String getPassword() { return password; }
        public Integer getDatabase() { return database; }
    }

    @Bean
    public RedisProps redisProps() {
        return new RedisProps();
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(RedisProps p) {
        RedisStandaloneConfiguration standalone = new RedisStandaloneConfiguration();
        standalone.setHostName(p.getHost());
        standalone.setPort(p.getPort());
        if (p.getPassword() != null && !p.getPassword().isBlank()) {
            standalone.setPassword(RedisPassword.of(p.getPassword()));
        }
        if (p.getDatabase() != null) {
            standalone.setDatabase(p.getDatabase());
        }

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(p.getTimeoutMs()))
                .shutdownTimeout(Duration.ofSeconds(2))
                .build();

        return new LettuceConnectionFactory(standalone, clientConfig);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }
}
