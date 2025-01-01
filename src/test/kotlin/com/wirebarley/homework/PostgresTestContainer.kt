package com.wirebarley.homework

import org.springframework.test.context.ContextConfiguration

@Target(allowedTargets = [AnnotationTarget.TYPE, AnnotationTarget.CLASS])
@Retention(value = AnnotationRetention.RUNTIME)
@ContextConfiguration(initializers = [PostgresTestContainerInitializer::class])
annotation class PostgresTestContainer
