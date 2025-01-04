package com.wirebarley.homework.services.transfer.statement

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 이체거래 처리결과 객체.
 *
 * @property originAccountId 출금대상 계좌
 * @property targetAccountId 입금대상 계좌
 * @property amount 이체액
 * @property feeAmount 수수료
 * @property transactionAt 거래일시
 */
data class TransferStatement(
  val originAccountId: Long,
  val targetAccountId: Long,
  val amount: BigDecimal,
  val feeAmount: BigDecimal,
  val transactionAt: LocalDateTime
)
