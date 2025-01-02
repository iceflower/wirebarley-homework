package com.wirebarley.homework.services.account.command

/**
 * 새로운 계좌 생성 명령서 객체.
 *
 * @property name 예금주 성명
 * @property phoneNumber 예금주 전화번호
 * @property email 예금주 이메일
 * @property requester 요청자 정보
 */
data class CreateNewAccountCommand(
  val name: String,
  val phoneNumber: String,
  val email: String,
  val requester: String
)
