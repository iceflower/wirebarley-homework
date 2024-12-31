package com.wirebarley.homework.jpa.entities.account

import com.wirebarley.homework.jpa.entities.common.Audit
import com.wirebarley.homework.vo.account.AccountInfo
import com.wirebarley.homework.vo.common.UserType
import io.hypersistence.utils.hibernate.id.Tsid
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import java.math.BigDecimal

/**
 * 계좌 정보 엔티티
 *
 * @property id 계좌번호
 * @property ownerName 예금주 성함
 * @property ownerPhoneNumber 예금주 전화번호
 * @property ownerEmail 예금주 이메일
 * @property userType 예금주 유형
 * @property totalAmount 현재 계좌 잔고
 * @property audit 감사 로그
 */
@Entity
class Accounts(
  @Id @Tsid
  @Column(name = "account_id")
  var id: Long?,

  @Column(name = "account_owner_name")
  val ownerName: String,

  @Column(name = "account_owner_phone_number", unique = true)
  val ownerPhoneNumber: String,

  @Column(name = "account_owner_email", unique = true)
  val ownerEmail: String,

  @Enumerated(EnumType.STRING)
  @Column(name = "user_type")
  val userType: UserType,

  @Column(name = "total_amount")
  var totalAmount: BigDecimal,

  @Embedded
  val audit: Audit
) {

  companion object {

    /**
     * 새로운 은행계좌 엔티티를 생성합니다.
     *
     * @param ownerName 예금주 성함
     * @param ownerPhoneNumber 예금주 연락처
     * @param ownerEmail 예금주 이메일
     * @param requester 개설 요청자
     * @return 새로 생성한 은행계좌 엔티티
     */
    fun createNewAccount(
      ownerName: String,
      ownerPhoneNumber: String,
      ownerEmail: String,
      requester: String
    ): Accounts {

      return Accounts(
        null,
        ownerName,
        ownerPhoneNumber,
        ownerEmail,
        UserType.NORMAL_CUSTOMER,
        BigDecimal.ZERO,
        Audit.create(requester)
      )
    }
  }

  /**
   * 계좌 잔고를 변경합니다.
   *
   * @param totalAmount 계좌잔고
   * @param requester 계좌잔고 변경 요청자
   */
  fun correctAmount(totalAmount: BigDecimal, requester: String) {
    this.totalAmount = totalAmount
    this.audit.update(requester)
  }

  fun toVo() = AccountInfo(
    this.id!!,
    this.ownerName,
    this.ownerPhoneNumber,
    this.ownerEmail,
    this.totalAmount
  )
}
