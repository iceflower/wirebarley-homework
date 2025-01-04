package com.wirebarley.homework.util.lock.distributed

import org.aspectj.lang.ProceedingJoinPoint
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

/**
 * @RedisDistributedLock 선언 시 수행되는 트랜잭션 객체입니다.
 */
@Component
class RedisDistributedRockForTransaction {

  /**
   * readonly 가 아닌 트랜잭션을 확성화시켜주는 메소드입니다.
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  fun notReadOnlyProceed(joinPoint: ProceedingJoinPoint): Any = joinPoint.proceed()

  /**
   * readonly인 트랜잭션을 확성화시켜주는 메소드입니다.
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
  fun readOnlyProceed(joinPoint: ProceedingJoinPoint): Any = joinPoint.proceed()
}
