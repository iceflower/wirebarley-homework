package com.wirebarley.homework.services.transaction.deposit.statement

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 입금거래 처리결과 객체.
 *
 * @property targetAccountId 입금대상 계좌
 * @property amount 입금액
 * @property feeAmount 수수료
 * @property transactionAt 거래일시
 */
data class DepositStatement(
  val targetAccountId: Long,
  val amount: BigDecimal,
  val feeAmount: BigDecimal,
  val transactionAt: LocalDateTime
)
