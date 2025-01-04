package com.wirebarley.homework.controllers.common.response

import com.wirebarley.homework.controllers.common.response.vo.DetailedFormatError
import com.wirebarley.homework.controllers.common.response.vo.FormatErrorDesc

/**
 * 유효하지 않은 요청 포맷 응답용 값 객체 (VO).
 *
 * @property data 응답 본문 (null)
 * @property error 오류 명세
 */
data class InvalidFormatErrorResponse(
  override val data: String?,
  override val error: FormatErrorDesc?
): Response<String, FormatErrorDesc>(data, error) {

  companion object {
    fun badRequest(detailed: List<DetailedFormatError>): InvalidFormatErrorResponse {

      return InvalidFormatErrorResponse(
        null,
        FormatErrorDesc(
          "INVALID_FORMAT",
          "요청 포맷이 올바르지 않습니다.",
          detailed
        )
      )
    }
  }
}
