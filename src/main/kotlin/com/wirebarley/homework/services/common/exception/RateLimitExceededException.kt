package com.wirebarley.homework.services.common.exception

import com.wirebarley.homework.vo.common.TransactionType

/**
 * 이체 / 출금 한도를 초과했을 때 던져지는 예외입니다.
 */
class RateLimitExceededException(val transactionType: TransactionType, override val message: String?) : RuntimeException()  {
}
