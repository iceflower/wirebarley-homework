package com.wirebarley.homework.controllers.common.response.vo

/**
 * API 오류 응답시 사용하는 오류 명세용 값 객체.
 *
 * @property code 오류 코드
 * @property message 오류 메시지
 */
data class BasicErrorDesc(
  val code: String,
  val message: String
)
