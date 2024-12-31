package com.wirebarley.homework.vo.transaction

import com.wirebarley.homework.vo.common.TransactionChannel
import java.math.BigDecimal

/**
 * 거래 정보를 정의하기 위한 sealed class.
 *
 * @property transactionId 거래 ID
 * @property transactionChannel 거래요청 채널
 * @property feeAmount 거래수수료
 * @property amount 거래금액
 */
sealed class TransactionInfo {
  abstract val transactionId: String
  abstract val transactionChannel: TransactionChannel
  abstract val feeAmount: BigDecimal
  abstract val amount: BigDecimal
}

/**
 * 입금 정보를 정의하기 위한 VO.
 *
 * @property transactionId 거래 ID
 * @property transactionChannel 거래요청 채널
 * @property accountId 입금대상 계좌
 * @property feeAmount 입금 수수료
 * @property amount 앱금 금액
 */
data class DepositInfo(
  override val transactionId: String,
  override val transactionChannel: TransactionChannel,
  val accountId: Long,
  override val feeAmount: BigDecimal,
  override val amount: BigDecimal
) : TransactionInfo()

/**
 * 출금 정보를 정의하기 위한 VO.
 *
 * @property transactionId 거래 ID
 * @property transactionChannel 거래요청 채널
 * @property accountId 출금대상 계좌
 * @property feeAmount 출금수수료
 * @property amount 출금 금액
 */
data class WithdrawalInfo(
  override val transactionId: String,
  override val transactionChannel: TransactionChannel,
  val accountId: Long,
  override val feeAmount: BigDecimal,
  override val amount: BigDecimal
) : TransactionInfo()

/**
 * 이체 정보를 정의하기 위한 VO.
 *
 * @property transactionId 거래 ID
 * @property transactionChannel 거래요청 채널
 * @property originAccountId 이체를 진행하는 계좌
 * @property targetAccountId 이체 대상 계좌
 * @property feeAmount 이체 수수료
 * @property amount 이체 금액
 */
data class TransferInfo(
  override val transactionId: String,
  override val transactionChannel: TransactionChannel,
  val originAccountId: Long,
  val targetAccountId: Long,
  override val feeAmount: BigDecimal,
  override val amount: BigDecimal
) : TransactionInfo()
