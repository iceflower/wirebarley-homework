package com.wirebarley.homework.controllers.transaction.request

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

/**
 * 입금요청 객체.
 *
 * @property targetAccountId 입금계좌
 * @property amount 입금액
 * @property email 요청자 email
 */
data class DepositRequest(

  @field:NotNull(message = "입금계좌는 필수값입니다.")
  @field:Min(value = 10000000000000000, message = "입금계좌는 [10000000000000000] 이상의 17자리 숫자여야 합니다.")
  val targetAccountId: Long?,

  @field:NotNull(message = "입금액은 필수값입니다.")
  @field:Min(value = 1, message = "입금액은 1원 이상의 숫자여야 합니다.")
  val amount: Long?,

  @field:NotNull(message = "이메일은 필수값입니다.")
  @field:Pattern(
    regexp = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}\$",
    message = "xxx@xxx.com, xxx@.xxx.co.kr 등과 같은 이메일 주소 형식만 입력이 가능합니다."
  )
  val email: String?
)
