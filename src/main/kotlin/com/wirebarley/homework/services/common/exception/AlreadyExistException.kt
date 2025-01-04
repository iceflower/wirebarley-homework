package com.wirebarley.homework.services.common.exception

/**
 * 이미 데이터베이스에 존재하는 데이터를 발견했을 때 던져지는 예외 클래스입니다.
 */
class AlreadyExistException(val dataType: ExistDataType, override val message: String?): RuntimeException()

enum class ExistDataType {
  PHONE_NUMBER, EMAIL, UNKNOWN
}
