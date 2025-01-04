package com.wirebarley.homework.services.account

import com.wirebarley.homework.jpa.entities.account.AccountsRepository
import com.wirebarley.homework.jpa.entities.transaction.TransactionsRepository
import com.wirebarley.homework.services.common.exception.NotFoundDataType
import com.wirebarley.homework.services.common.exception.NotFoundException
import com.wirebarley.homework.util.lock.distributed.RedisDistributedLock
import com.wirebarley.homework.vo.transaction.TransactionInfo
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class AccountTransactionsInformant(
  private val accountsRepository: AccountsRepository,
  private val transactionsRepository: TransactionsRepository
) {

  /**
   * 계좌번호와 페이징 객체가 주어지면, 주어진 계좌번호의 거래내역을 페이징 처리하여 반환합니다.
   *
   * @param accountId 거래내역 조회대상 계좌번호
   * @return 페이징 처리된 거래내역
   */
  @RedisDistributedLock(key = "#query-account-transactions", timeUnit = TimeUnit.MICROSECONDS, waitTime = 150L, leaseTime = 100L, readOnly = true)
  fun transactions(accountId: Long, pageable: Pageable): Page<TransactionInfo> {

    val accountIdExists = accountsRepository.existById(accountId)

    if (!accountIdExists) {
      throw NotFoundException(NotFoundDataType.ACCOUNT_ID, "계좌번호를 찾을 수 없습니다.")
    }

    val pagedList = transactionsRepository.findAllTransactions(accountId, pageable)
    val voContents = pagedList.content.map { it.toVo() }

    return PageImpl(voContents, pageable, pagedList.totalElements)
  }
}
