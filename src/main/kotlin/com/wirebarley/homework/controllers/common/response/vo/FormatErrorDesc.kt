package com.wirebarley.homework.controllers.common.response.vo

/**
 * 요청 포맷 오류 발생시 사용하는 오류 명세용 VO.
 *
 * @param code 오류코드
 * @param message 오류메시지
 * @param detailed 상세 오류
 */
data class FormatErrorDesc(val code: String, val message: String, val detailed: List<DetailedFormatError>)
