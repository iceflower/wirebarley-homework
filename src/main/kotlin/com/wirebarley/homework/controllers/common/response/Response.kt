package com.wirebarley.homework.controllers.common.response

/**
 * API 응답용 값 객체.
 *
 * @property data 응답 본문 (응답데이터가 없는 정상 응답이거나, 오류 응답일 경우 null)
 * @property error 오류 명세 (오류 응답이 아닐 경우 null)
 */
abstract class Response<T, E>(open val data: T?, open val error: E?)
