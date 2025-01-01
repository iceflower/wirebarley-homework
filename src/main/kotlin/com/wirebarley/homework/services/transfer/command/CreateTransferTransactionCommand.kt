package com.wirebarley.homework.services.transfer.command

import com.wirebarley.homework.vo.common.TransactionChannel
import java.math.BigDecimal

/**
 * 이체 거래 명령서 객체.
 *
 * @property originAccountId 출금계좌
 * @property targetAccountId 입금계좌
 * @property amount 이체금액
 * @property transactionChannel 거래채널
 * @property requester 요청자
 */
data class CreateTransferTransactionCommand(
  val originAccountId: Long,
  val targetAccountId: Long,
  val amount: BigDecimal,
  val transactionChannel: TransactionChannel,
  val requester: String
)
