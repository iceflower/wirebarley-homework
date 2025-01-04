package com.wirebarley.homework.services.transaction.withdrawal.command

import com.wirebarley.homework.vo.common.TransactionChannel
import java.math.BigDecimal

/**
 * 풀금 거래 명령서 객체.
 *
 * @property originAccountId 출금계좌
 * @property amount 출금액
 * @property transactionChannel 거래채널
 * @property requester 요청자
 */
data class CreateWithdrawalTransactionCommand(
  val originAccountId: Long,
  val amount: BigDecimal,
  val transactionChannel: TransactionChannel,
  val requester: String
)
