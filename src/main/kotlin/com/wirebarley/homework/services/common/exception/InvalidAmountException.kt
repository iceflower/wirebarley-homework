package com.wirebarley.homework.services.common.exception

/**
 * 입금 / 출금 / 이체 금액 관련 문제가 발생했을 때 던져지는 예외입니다.
 */
class InvalidAmountException(override val message: String?) : RuntimeException()

