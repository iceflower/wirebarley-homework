package com.wirebarley.homework.controllers.common.response.vo

/**
 * 요청 포맷 오류 발생시 사용하는, 상세 오류 명세용 값 객체.
 *
 * @property fieldName 오류가 발생한 필드 이름
 * @property errorMessage 오류메시지
 */
data class DetailedFormatError(val fieldName: String, val errorMessage: String)
