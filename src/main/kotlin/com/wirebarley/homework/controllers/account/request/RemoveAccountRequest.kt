package com.wirebarley.homework.controllers.account.request

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern


/**
 * 계좌 삭제 요청 객체.
 *
 * @property accountId 삭제대상 계좌
 * @property email 삭제 요청자
 */

data class RemoveAccountRequest(

  @field:NotNull(message = "계좌번호는 필수값입니다.")
  @field:Min(value = 10000000000000000, message = "계좌번호는 [10000000000000000] 이상의 17자리 숫자여야 합니다.")
  val accountId: Long?,

  @field:NotNull(message = "이메일은 필수값입니다.")
  @field:Pattern(
    regexp = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}\$",
    message = "xxx@xxx.com, xxx@.xxx.co.kr 등과 같은 이메일 주소 형식만 입력이 가능합니다."
  )
  val email: String?
)
