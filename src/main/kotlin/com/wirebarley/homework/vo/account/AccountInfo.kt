package com.wirebarley.homework.vo.account

import java.math.BigDecimal

/**
 * 계좌 정보 VO.
 *
 * @property id 계좌번호
 * @property ownerName 예금주 성함
 * @property phoneNumber 예금주 전화번호
 * @property email 예금주 이메일
 * @property totalAmount 예금주 잔고
 */
data class AccountInfo(
  val id: Long,
  val ownerName: String,
  val phoneNumber: String,
  val email: String,
  val totalAmount: BigDecimal
)
