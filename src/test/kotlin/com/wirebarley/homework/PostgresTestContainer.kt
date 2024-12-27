package com.wirebarley.homework

import org.springframework.test.context.ContextConfiguration

@Target(allowedTargets = [AnnotationTarget.TYPE])
@Retention(value = AnnotationRetention.RUNTIME)
@ContextConfiguration(initializers = [PostgresTestContainerInitializer::class])
annotation class PostgresTestContainer
