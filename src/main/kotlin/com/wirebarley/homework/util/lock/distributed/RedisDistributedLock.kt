package com.wirebarley.homework.util.lock.distributed

import java.util.concurrent.TimeUnit

/**
 * 레디스 분산락을 활성화시키기 위해 사용하는 어노테이션입니다.
 *
 * @property key 분산락 키 이름
 * @property timeUnit 분산락 시간 단위 (기본값 : SECONDS)
 * @property waitTime 분산락 최대 대기 시간 (기본값 : 5)
 * @property leaseTime 분산락을 걸 수 있는 최대 시간 (기본값 : 3)
 */
@Target(allowedTargets = [AnnotationTarget.FUNCTION])
@Retention(value = AnnotationRetention.RUNTIME)
annotation class RedisDistributedLock(
  val key: String, val timeUnit: TimeUnit = TimeUnit.SECONDS, val waitTime: Long = 5L, val leaseTime: Long = 3L,
  val readOnly: Boolean = false
)
