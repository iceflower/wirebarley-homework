package com.wirebarley.homework.util.lock.distributed

import java.util.concurrent.TimeUnit


@Target(allowedTargets = [AnnotationTarget.FUNCTION])
@Retention(value = AnnotationRetention.RUNTIME)
annotation class RedisDistributedLock(val key: String, val timeUnit: TimeUnit = TimeUnit.SECONDS, val waitTime: Long = 5L, val leaseTime: Long = 3L)
