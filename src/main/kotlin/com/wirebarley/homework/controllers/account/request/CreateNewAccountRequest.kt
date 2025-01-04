package com.wirebarley.homework.controllers.account.request

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import org.hibernate.validator.constraints.Length

/**
 * 계좌 신규생성 요청 객체.
 *
 * @property name 예금주 성명
 * @property phoneNumber 예금주 전화번호
 * @property email 예금주 이메일
 */
data class CreateNewAccountRequest(
  @field:NotNull(message = "이름은 필수값입니다.")
  @field:Length(min = 1, max = 16, message = "이름은 1자 이상, 16자 이하여야 합니다")
  @field:Pattern(
    regexp = "^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9]*\$",
    message = "이름은 [알파벳(대/소문자), 한글, 숫자(0~9)] 만 허용됩니다."
  )
  val name: String?,

  @field:NotNull(message = "전화번호는 필수값입니다.")
  @field:Pattern(
    regexp = "^((02|010|031|032|033|041|042|043|044|051|052|053|054|055|061|062|063|064|070)-\\d{3,4}-\\d{4})\$",
    message = "'000-0000-0000 형식의 유선전화/인터넷전화/휴대폰전화 번호만 입력이 가능합니다."
  )
  val phoneNumber: String?,

  @field:NotNull(message = "이메일은 필수값입니다.")
  @field:Pattern(
    regexp = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}\$",
    message = "xxx@xxx.com, xxx@.xxx.co.kr 등과 같은 이메일 주소 형식만 입력이 가능합니다."
  )
  val email: String?
)
