package com.wirebarley.homework.controllers.account.response

import com.wirebarley.homework.controllers.common.response.Response
import com.wirebarley.homework.services.account.statememt.RegisteredAccountStatement
import com.wirebarley.homework.services.account.statememt.RemovedAccountStatement


/**
 * 계좌 삭제 결과 응답용 객체
 *
 * @property data 계좌 삭제 결과 명세 (오류 발생시 null)
 * @property error 오류 명세 (정상 응답시 null)
 */
data class RemoveAccountResponse(
  override val data: RemovedAccountStatement?,
  override val error: String?
) : Response<RemovedAccountStatement, String>(data, error) {

  companion object {
    fun ok(data: RemovedAccountStatement) = RemoveAccountResponse(data, null)
  }
}
