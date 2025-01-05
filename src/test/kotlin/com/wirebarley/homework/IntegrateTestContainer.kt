package com.wirebarley.homework

import org.springframework.test.context.ContextConfiguration

@Target(allowedTargets = [AnnotationTarget.TYPE, AnnotationTarget.CLASS])
@Retention(value = AnnotationRetention.RUNTIME)
@ContextConfiguration(initializers = [IntegrateTestContainerInitializer::class])
annotation class IntegrateTestContainer
