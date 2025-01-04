package com.wirebarley.homework.services.common.exception

/**
 * 특정 데이터를 찾을 수 없을 경우 던져지는 예외입니다.
 */
class NotFoundException(val notFoundDataType: NotFoundDataType, override val message: String?) : RuntimeException()

enum class NotFoundDataType {
  ACCOUNT_ID, TRANSACTION_CHANNEL, UNKNOWN
}
