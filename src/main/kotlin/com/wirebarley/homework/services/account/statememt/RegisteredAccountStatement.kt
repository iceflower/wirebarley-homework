package com.wirebarley.homework.services.account.statememt

import java.time.LocalDateTime

/**
 * 등록된 계좌 정보 응답용 객체.
 *
 * @property accountId 등록된 계좌번호
 * @property registeredAt 등록일시
 */
data class RegisteredAccountStatement(
  val accountId: Long,
  val registeredAt: LocalDateTime
)
