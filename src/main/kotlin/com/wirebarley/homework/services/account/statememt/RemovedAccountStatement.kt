package com.wirebarley.homework.services.account.statememt

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

/**
 * 제거된 계좌 정보 응답용 객체.
 *
 * @property accountId 제거된 계좌번호
 * @property removedAt 제거 일시
 */
data class RemovedAccountStatement(
  val accountId: Long,
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  val removedAt: LocalDateTime
)
