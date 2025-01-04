package com.wirebarley.homework.vo.common

import java.math.BigDecimal

/**
 * 거래 유형.
 *
 * @property feeRatio 거래수수료율
 * @property amountLimit 거래한도
 */
enum class TransactionType(val feeRatio: BigDecimal, val amountLimit: Long) {
  DEPOSIT(BigDecimal.ZERO, Long.MAX_VALUE), // 입금
  WITHDRAWAL(BigDecimal.ZERO, 1000000), // 출금
  TRANSFER(BigDecimal("0.01"), 3000000), // 이체
  UNKNOWN(BigDecimal.ZERO, 0) // 정의되지 않은 거래유형
}
