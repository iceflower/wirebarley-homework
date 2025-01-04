package com.wirebarley.homework.controllers.transaction.response

import com.wirebarley.homework.controllers.common.response.Response
import com.wirebarley.homework.services.transaction.deposit.statement.DepositStatement

/**
 * 입금결과 응답 객체.
 *
 * @property data 입금결과 명세 (오류 응답일 경우 null)
 * @property error 오류 명세 (정상 응답일 경우 null)
 */
data class DepositResponse(
  override val data: DepositStatement?,
  override val error: String?
) : Response<DepositStatement, String>(data, error) {

  companion object {
    fun ok(data: DepositStatement) = DepositResponse(data, null)
  }
}
