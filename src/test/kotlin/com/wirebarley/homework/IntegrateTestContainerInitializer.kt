package com.wirebarley.homework

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer

class IntegrateTestContainerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
  private val postgresContainer: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:17.2-alpine3.21")
    .withDatabaseName("unit-test")
    .withUsername("test")
    .withPassword("test")

  private val redisContainer: GenericContainer<*> = GenericContainer("redis:7.4.1-alpine")
    .withExposedPorts(6379)

  override fun initialize(ctx: ConfigurableApplicationContext) {
    postgresContainer.start()
    redisContainer.start()

    TestPropertyValues.of(
      "spring.datasource.url=" + postgresContainer.jdbcUrl,
      "spring.datasource.username=" + postgresContainer.username,
      "spring.datasource.password=" + postgresContainer.password,
      "spring.datasource.driver-class-name=org.postgresql.Driver",
      "spring.data.redis.host=${redisContainer.host}",
      "spring.data.redis.port=${redisContainer.getMappedPort(6379)}",
      "spring.data.redis.client-type=lettuce",
    ).applyTo(ctx.environment)
  }


}
