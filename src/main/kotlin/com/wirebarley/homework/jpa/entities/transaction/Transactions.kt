package com.wirebarley.homework.jpa.entities.transaction

import com.wirebarley.homework.jpa.entities.account.Accounts
import com.wirebarley.homework.jpa.entities.common.Audit
import com.wirebarley.homework.vo.common.TransactionChannel
import com.wirebarley.homework.vo.common.TransactionType
import com.wirebarley.homework.vo.transaction.TransactionInfo
import io.hypersistence.utils.hibernate.id.Tsid
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.math.BigDecimal
import java.math.RoundingMode


/**
 * 거래내역 엔티티.
 *
 * @property id 거래내역 ID
 * @property transactionType 거래 유형
 * @property transactionChannel 거래 채널
 * @property originAccount 송금 계좌
 * @property targetAccount 수금 계좌
 * @property amount 거래금액
 * @property fee 거래수수료
 * @property audit 감사로그
 */
@Entity
class Transactions(
  @Id
  @Tsid
  @Column(name = "transaction_id")
  var id: String?,

  @Enumerated(EnumType.STRING)
  @Column(name = "transaction_type", nullable = false)
  val transactionType: TransactionType,

  @Enumerated(EnumType.STRING)
  @Column(name = "transaction_channel", nullable = false)
  val transactionChannel: TransactionChannel,

  @ManyToOne
  @JoinColumn(
    name = "origin_account_id",
    referencedColumnName = "account_id"
  )
  val originAccount: Accounts?,

  @ManyToOne
  @JoinColumn(
    name = "target_account_id",
    referencedColumnName = "account_id"
  )
  val targetAccount: Accounts?,

  @Column(name = "amount")
  val amount: BigDecimal,

  @Column(name = "fee_amount")
  val fee: BigDecimal,

  @Embedded
  val audit: Audit
) {

  companion object {

    /**
     * 새로운 입금거래 엔티티를 생성합니다.
     *
     * @param transactionChannel 거래 채널 (온라인, 은행창구, ATM)
     * @param targetAccount 입금대상 계좌 엔티티
     * @param amount 입금액
     * @param requester 입금 요청자
     * @return 새로 생성한 입금 엔티티
     */
    fun makeNewDepositTransaction(
      transactionChannel: TransactionChannel,
      targetAccount: Accounts,
      amount: BigDecimal,
      requester: String
    ): Transactions {
      val feeAmount = amount * TransactionType.DEPOSIT.feeRatio

      return Transactions(
        null,
        TransactionType.DEPOSIT,
        transactionChannel,
        null,
        targetAccount,
        amount,
        feeAmount.setScale(1, RoundingMode.DOWN),
        Audit.create(requester)
      )
    }

    /**
     * 새로운 출금 엔티티를 생성합니다.
     *
     * @param transactionChannel 거래 채널 (온라인, 은행창구, ATM)
     * @param originAccount 출금대상 계좌 엔티티
     * @param amount 출금액
     * @param requester 출금 요청자
     * @return 새로 생성한 출금 엔티티
     */
    fun makeNewWithdrawalTransaction(
      transactionChannel: TransactionChannel,
      originAccount: Accounts,
      amount: BigDecimal,
      requester: String
    ): Transactions {
      val feeAmount = amount * TransactionType.WITHDRAWAL.feeRatio
      return Transactions(
        null,
        TransactionType.WITHDRAWAL,
        transactionChannel,
        originAccount,
        null,
        amount,
        feeAmount.setScale(1, RoundingMode.DOWN),
        Audit.create(requester)
      )
    }

    /**
     * 새로운 계좌이체 엔티티를 생성합니다.
     *
     * @param transactionChannel 거래 채널 (온라인, 은행창구, ATM)
     * @param originAccount 계좌이체를 하는 계좌 엔티티
     * @param targetAccount 계좌이체를 받는 계좌 엔티티
     * @param amount 출금액
     * @param requester 출금 요청자
     * @return 새로 생성한 이체 엔티티
     */
    fun makeNewTransferTransaction(
      transactionChannel: TransactionChannel,
      originAccount: Accounts,
      targetAccount: Accounts,
      amount: BigDecimal,
      requester: String
    ): Transactions {
      val feeAmount = amount * TransactionType.TRANSFER.feeRatio
      return Transactions(
        null,
        TransactionType.TRANSFER,
        transactionChannel,
        originAccount,
        targetAccount,
        amount,
        feeAmount.setScale(1, RoundingMode.DOWN),
        Audit.create(requester)
      )
    }
  }

  /**
   * 엔티티를 VO로 변환하여 돌려줍니다.
   *
   * @return 입금 거래일 경우 `DepositInfo`, 출금 거래일 경우 `WithdrawalInfo`, 이체 거래일 경우 `TransferInfo`
   */
  fun toVo(): TransactionInfo? {
    return when (this.transactionType) {
      TransactionType.DEPOSIT -> TransactionInfo.deposit(
        this.id!!,
        this.transactionChannel,
        this.targetAccount!!.id!!,
        this.fee,
        this.amount,
        this.audit.updatedAt
      )

      TransactionType.WITHDRAWAL -> TransactionInfo.withdrawal(
        this.id!!,
        this.transactionChannel,
        this.originAccount!!.id!!,
        this.fee,
        this.amount,
        this.audit.updatedAt
      )

      TransactionType.TRANSFER -> TransactionInfo.transfer(
        this.id!!,
        this.transactionChannel,
        this.originAccount!!.id!!,
        this.targetAccount!!.id!!,
        this.fee,
        this.amount,
        this.audit.updatedAt
      )

      else -> null
    }
  }
}
