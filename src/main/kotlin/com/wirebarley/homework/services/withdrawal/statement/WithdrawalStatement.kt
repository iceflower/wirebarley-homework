package com.wirebarley.homework.services.withdrawal.statement

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 출금거래 처리결과 객체.
 *
 * @property originAccountId 출금대상 계좌
 * @property amount 이체액
 * @property feeAmount 수수료
 * @property transactionAt 거래일시
 */
data class WithdrawalStatement(
  val originAccountId: Long,
  val amount: BigDecimal,
  val feeAmount: BigDecimal,
  val transactionAt: LocalDateTime
)
