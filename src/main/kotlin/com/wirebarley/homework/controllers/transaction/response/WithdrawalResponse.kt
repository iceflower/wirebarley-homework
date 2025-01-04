package com.wirebarley.homework.controllers.transaction.response

import com.wirebarley.homework.controllers.common.response.Response
import com.wirebarley.homework.services.transaction.withdrawal.statement.WithdrawalStatement

/**
 * 출금 결과 응답 객체.
 *
 * @property data 출금 결과 명세 (오류 응답일 경우 null)
 * @property error 오류 명세 (정상 응답일 경우 null)
 */
data class WithdrawalResponse(
  override val data: WithdrawalStatement?,
  override val error: String?
) : Response<WithdrawalStatement, String>(data, error) {

  companion object {
    fun ok(data: WithdrawalStatement) = WithdrawalResponse(data, null)
  }
}
