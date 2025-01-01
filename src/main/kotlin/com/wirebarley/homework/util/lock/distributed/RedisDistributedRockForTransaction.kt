package com.wirebarley.homework.util.lock.distributed

import org.aspectj.lang.ProceedingJoinPoint
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class RedisDistributedRockForTransaction {

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  fun proceed(joinPoint: ProceedingJoinPoint): Any = joinPoint.proceed()
}
