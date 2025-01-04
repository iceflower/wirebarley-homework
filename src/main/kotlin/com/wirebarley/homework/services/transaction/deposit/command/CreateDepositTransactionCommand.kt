package com.wirebarley.homework.services.transaction.deposit.command

import com.wirebarley.homework.vo.common.TransactionChannel
import java.math.BigDecimal


/**
 * 입금 거래 명령서 객체.
 *
 * @property targetAccountId 입금대상 계좌
 * @property amount 입금액
 * @property transactionChannel 거래채널
 * @property requester 요청자
 */
data class CreateDepositTransactionCommand(
  val targetAccountId: Long,
  val amount: BigDecimal,
  val transactionChannel: TransactionChannel,
  val requester: String
)
