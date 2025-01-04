package com.wirebarley.homework.controllers.common.response

import com.wirebarley.homework.controllers.common.response.vo.BasicErrorDesc

/**
 * 오류 발생시 사용하는 오류 명세용 객체.
 *
 * @param data 응답 본문 (null)
 * @param error 오류코드 및 메시지
 */
data class BasicErrorResponse(
  override val data: String?,
  override val error: BasicErrorDesc?
) : Response<String, BasicErrorDesc>(data, error) {

  companion object {
    fun badRequest(code: String, message: String): BasicErrorResponse {

      return BasicErrorResponse(
        null,
        BasicErrorDesc(
          code, message,
        )
      )
    }
  }
}
