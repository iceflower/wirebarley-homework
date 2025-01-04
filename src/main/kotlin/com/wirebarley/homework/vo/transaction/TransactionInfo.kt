package com.wirebarley.homework.vo.transaction

import com.fasterxml.jackson.annotation.JsonFormat
import com.wirebarley.homework.vo.common.TransactionChannel
import com.wirebarley.homework.vo.common.TransactionType
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 거래 정보를 정의하기 위한 sealed class.
 *
 * @property transactionId 거래 ID
 * @property transactionChannel 거래요청 채널
 * @property transactionType 거래유형
 * @property feeAmount 거래수수료
 * @property amount 거래금액
 * @property transactionAt 거래일시
 */
sealed class TransactionInfo {
  abstract val transactionId: String
  abstract val transactionChannel: TransactionChannel
  abstract val transactionType: TransactionType
  abstract val feeAmount: BigDecimal
  abstract val amount: BigDecimal
  abstract val transactionAt: LocalDateTime

  companion object {

    /**
     * 입금 거래 객체를 생성합니다.
     *
     * @param transactionId 거래 ID
     * @param transactionChannel 거래요청 채널
     * @param targetAccountId 입금계좌
     * @param feeAmount 거래수수료
     * @param amount 거래금액
     * @param transactionAt 거래일시
     *
     * @return 입금거래 객체 (DepositInfo)
     */
    fun deposit(
      transactionId: String,
      transactionChannel: TransactionChannel,
      targetAccountId: Long,
      feeAmount: BigDecimal,
      amount: BigDecimal,
      transactionAt: LocalDateTime
    ) = DepositInfo(
      transactionId,
      transactionChannel,
      TransactionType.DEPOSIT,
      targetAccountId,
      feeAmount,
      amount,
      transactionAt
    )

    /**
     * 출금 거래 객체를 생성합니다.
     *
     * @param transactionId 거래 ID
     * @param transactionChannel 거래요청 채널
     * @param originAccountId 출금계좌
     * @param feeAmount 거래수수료
     * @param amount 거래금액
     * @param transactionAt 거래일시
     *
     * @return 출금거래 객체 (DepositInfo)
     */
    fun withdrawal(
      transactionId: String,
      transactionChannel: TransactionChannel,
      originAccountId: Long,
      feeAmount: BigDecimal,
      amount: BigDecimal,
      transactionAt: LocalDateTime
    ) = WithdrawalInfo(
      transactionId,
      transactionChannel,
      TransactionType.WITHDRAWAL,
      originAccountId,
      feeAmount,
      amount,
      transactionAt
    )

    /**
     * 이체 거래 객체를 생성합니다.
     *
     * @param transactionId 거래 ID
     * @param transactionChannel 거래요청 채널
     * @param originAccountId 출금계좌
     * @param targetAccountId 입금계좌
     * @param feeAmount 거래수수료
     * @param amount 거래금액
     * @param transactionAt 거래일시
     *
     * @return 입금거래 객체 (DepositInfo)
     */
    fun transfer(
      transactionId: String,
      transactionChannel: TransactionChannel,
      originAccountId: Long,
      targetAccountId: Long,
      feeAmount: BigDecimal,
      amount: BigDecimal,
      transactionAt: LocalDateTime
    ) = TransferInfo(
      transactionId,
      transactionChannel,
      TransactionType.TRANSFER,
      originAccountId,
      targetAccountId,
      feeAmount,
      amount,
      transactionAt
    )
  }
}

/**
 * 입금 정보를 정의하기 위한 VO.
 *
 * @property transactionId 거래 ID
 * @property transactionChannel 거래요청 채널
 * @property transactionType 거래유형
 * @property accountId 입금대상 계좌
 * @property feeAmount 입금 수수료
 * @property amount 앱금 금액
 * @property transactionAt 거래일시
 */
data class DepositInfo(
  override val transactionId: String,
  override val transactionChannel: TransactionChannel,
  override val transactionType: TransactionType,
  val accountId: Long,
  override val feeAmount: BigDecimal,
  override val amount: BigDecimal,
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  override val transactionAt: LocalDateTime
) : TransactionInfo()

/**
 * 출금 정보를 정의하기 위한 VO.
 *
 * @property transactionId 거래 ID
 * @property transactionChannel 거래요청 채널
 * @property transactionType 거래유형
 * @property accountId 출금대상 계좌
 * @property feeAmount 출금수수료
 * @property amount 출금 금액
 * @property transactionAt 거래일시
 */
data class WithdrawalInfo(
  override val transactionId: String,
  override val transactionChannel: TransactionChannel,
  override val transactionType: TransactionType,
  val accountId: Long,
  override val feeAmount: BigDecimal,
  override val amount: BigDecimal,
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  override val transactionAt: LocalDateTime
) : TransactionInfo()

/**
 * 이체 정보를 정의하기 위한 VO.
 *
 * @property transactionId 거래 ID
 * @property transactionChannel 거래요청 채널
 * @property transactionType 거래유형
 * @property originAccountId 이체를 진행하는 계좌
 * @property targetAccountId 이체 대상 계좌
 * @property feeAmount 이체 수수료
 * @property amount 이체 금액
 * @property transactionAt 거래일시
 */
data class TransferInfo(
  override val transactionId: String,
  override val transactionChannel: TransactionChannel,
  override val transactionType: TransactionType,
  val originAccountId: Long,
  val targetAccountId: Long,
  override val feeAmount: BigDecimal,
  override val amount: BigDecimal,
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  override val transactionAt: LocalDateTime
) : TransactionInfo()
