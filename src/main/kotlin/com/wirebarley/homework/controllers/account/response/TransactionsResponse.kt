package com.wirebarley.homework.controllers.account.response

import com.wirebarley.homework.controllers.common.response.Response
import com.wirebarley.homework.vo.transaction.TransactionInfo
import org.springframework.data.domain.Page

/**
 * 계좌 거래내역 응답용 객체.
 *
 * @property data 페이징 처리된 거래내역 명세 (오류 발생시 null)
 * @property error 오류 명세 (정상 응답시 null)
 */
data class TransactionsResponse(
  override val data: Page<TransactionInfo>?,
  override val error: String?
): Response<Page<TransactionInfo>, String>(data, error) {

  companion object {
    fun ok(data: Page<TransactionInfo>) = TransactionsResponse(data, null)
  }
}
