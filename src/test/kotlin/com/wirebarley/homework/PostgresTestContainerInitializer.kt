package com.wirebarley.homework

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.PostgreSQLContainer

class PostgresTestContainerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
  private val postgresContainer: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:17.2-alpine3.21")
    .withDatabaseName("unit-test")
    .withUsername("test")
    .withPassword("test")

  override fun initialize(ctx: ConfigurableApplicationContext) {
    postgresContainer.start()
    TestPropertyValues.of(
      "spring.datasource.url=" + postgresContainer.jdbcUrl,
      "spring.datasource.username=" + postgresContainer.username,
      "spring.datasource.password=" + postgresContainer.password,
      "spring.sql.init.data-locations=classpath:db/postgres/dml.sql",
      "spring.sql.init.schema-locations=classpath:db/postgres/ddl.sql"
    ).applyTo(ctx.environment)
  }
}
