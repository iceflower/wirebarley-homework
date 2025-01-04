package com.wirebarley.homework.controllers.transaction.response

import com.wirebarley.homework.controllers.common.response.Response
import com.wirebarley.homework.services.transaction.transfer.statement.TransferStatement

/**
 * 이체 결과 응답 객체.
 *
 * @property data 이체 결과 명세 (오류 응답일 경우 null)
 * @property error 오류 명세 (정상 응답일 경우 null)
 */
data class TransferResponse(
  override val data: TransferStatement?,
  override val error: String?
) : Response<TransferStatement, String>(data, error) {

  companion object {
    fun ok(data: TransferStatement) = TransferResponse(data, null)
  }
}
