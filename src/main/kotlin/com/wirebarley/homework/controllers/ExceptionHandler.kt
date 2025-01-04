package com.wirebarley.homework.controllers

import com.wirebarley.homework.controllers.common.response.BasicErrorResponse
import com.wirebarley.homework.controllers.common.response.InvalidFormatErrorResponse
import com.wirebarley.homework.controllers.common.response.vo.DetailedFormatError
import com.wirebarley.homework.services.common.exception.AlreadyExistException
import com.wirebarley.homework.services.common.exception.ExistDataType
import com.wirebarley.homework.services.common.exception.InvalidAmountException
import com.wirebarley.homework.services.common.exception.NotFoundDataType
import com.wirebarley.homework.services.common.exception.NotFoundException
import com.wirebarley.homework.services.common.exception.RateLimitExceededException
import com.wirebarley.homework.vo.common.TransactionType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler {

  /**
   * 요청 데이터 포맷이 올바르지 않을 때 던져지는 예외를 처리합니다.
   *
   * @param e MethodArgumentNotValidException
   *
   * @return 400 오류와, 오류가 발생한 필드 관련 오류메시지
   */
  @ExceptionHandler(exception = [MethodArgumentNotValidException::class])
  fun handleMethodArgumentNotValidException(
    e: MethodArgumentNotValidException
  ): ResponseEntity<InvalidFormatErrorResponse> {

    val detailed = e.bindingResult.fieldErrors.map {
      DetailedFormatError(
        it.field,
        it.defaultMessage!!
      )
    }

    return ResponseEntity.badRequest()
      .body(InvalidFormatErrorResponse.badRequest(detailed))
  }

  /**
   * 신규 데이터 생성시, 중복이 불가능한 데이터가 이미 사용중일 때 던져지는 예외를 처리합니다.
   *
   * @param e AlreadyExistException
   *
   * @return 400 오류와, 오류가 발생한 필드 관련 오류메시지
   */
  @ExceptionHandler(exception = [AlreadyExistException::class])
  fun handleAlreadyExistException(
    e: AlreadyExistException
  ): ResponseEntity<BasicErrorResponse> {

    val error = when (e.dataType) {
      ExistDataType.EMAIL -> Pair("EMAIL_IS_ALREADY_EXIST", e.message!!)
      ExistDataType.PHONE_NUMBER -> Pair("PHONE_NUMBER_IS_ALREADY_EXIST", e.message!!)
      else -> Pair("ALREADY_EXIST", e.message ?: "이미 존재하는 데이터입니다.")
    }

    return ResponseEntity.badRequest()
      .body(BasicErrorResponse.badRequest(error.first, error.second))
  }

  /**
   * 계좌 입금/출금/이체 요청 처리시, 처리할 수 없는 금액정보를 전닫받았을 때 던져지는 예외를 처리합니다.
   *
   * @param e InvalidAmountException
   *
   * @return 400 오류와, 오류가 발생한 필드 관련 오류메시지
   */
  @ExceptionHandler(exception = [InvalidAmountException::class])
  fun handleInvalidAmountException(
    e: InvalidAmountException
  ): ResponseEntity<BasicErrorResponse> {

    return ResponseEntity.badRequest()
      .body(BasicErrorResponse.badRequest("INVALID_AMOUNT", e.message ?: ""))
  }

  /**
   * 거래 요청 처리시, 존재하지 않는 데이터를 전달받았을 때 던져지는 예외를 처리합니다.
   *
   * @param e NotFoundException
   *
   * @return 400 오류와, 오류가 발생한 필드 관련 오류메시지
   */
  @ExceptionHandler(exception = [NotFoundException::class])
  fun handleNotFoundException(
    e: NotFoundException
  ): ResponseEntity<BasicErrorResponse> {

    val error = when (e.notFoundDataType) {
      NotFoundDataType.ACCOUNT_ID -> Pair("ACCOUNT_IS_NOT_FOUND", e.message ?: "")
      else -> Pair("NOT_FOUND", e.message ?: "이미 존재하는 데이터입니다.")
    }

    return ResponseEntity.badRequest()
      .body(BasicErrorResponse.badRequest(error.first, error.second))
  }

  /**
   * 거래 요청 처리시, 일일 거래한도를 초과하였을 때 던져지는 예외를 처리합니다.
   *
   * @param e RateLimitExceededException
   *
   * @return 400 오류와, 오류가 발생한 필드 관련 오류메시지
   */
  @ExceptionHandler(exception = [RateLimitExceededException::class])
  fun handleRateLimitExceededException(
    e: RateLimitExceededException
  ): ResponseEntity<BasicErrorResponse> {

    val error = when (e.transactionType) {
      TransactionType.DEPOSIT -> Pair("DEPOSIT_RATE_LIMIT_EXCEEDED", e.message ?: "")
      TransactionType.TRANSFER -> Pair("TRANSFER_RATE_LIMIT_EXCEEDED", e.message ?: "")
      TransactionType.WITHDRAWAL -> Pair("WITHDRAWAL_RATE_LIMIT_EXCEEDED", e.message ?: "")
      else -> Pair("RATE_LIMIT_EXCEEDED", e.message ?: "사용 한도를 초과했습니다.")
    }

    return ResponseEntity.badRequest()
      .body(BasicErrorResponse.badRequest(error.first, error.second))
  }
}
