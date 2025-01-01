package com.wirebarley.homework.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories

/**
 * Redis
 */
@Configuration
@EnableRedisRepositories
class RedisConfig(
  @Value("\${spring.data.redis.host}")
  private val host: String,
  @Value("\${spring.data.redis.port}")
  private val port: String,
) {

  @Bean
  fun redissonClient(): RedissonClient {
    val config = Config()
    config.useSingleServer()
      .setAddress("redis://$host:$port")
    return Redisson.create(config)
  }
}
