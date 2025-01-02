package com.wirebarley.homework.services.account

import com.wirebarley.homework.jpa.entities.account.AccountsRepository
import com.wirebarley.homework.services.account.command.RemoveAccountCommand
import com.wirebarley.homework.services.common.exception.InvalidAmountException
import com.wirebarley.homework.services.common.exception.NotFoundDataType
import com.wirebarley.homework.services.common.exception.NotFoundException
import com.wirebarley.homework.util.lock.distributed.RedisDistributedLock
import java.math.BigDecimal

class AccountRemover(private val accountsRepository: AccountsRepository) {

  /**
   * 계좌를 삭제합니다.
   *
   * @param command 계좌 삭제 명령서 객체
   */
  @RedisDistributedLock(key = "#remove-account")
  fun remove(command: RemoveAccountCommand) {

    val accountIdExist = accountsRepository.existById(command.accountId)

    if (!accountIdExist) {
      throw NotFoundException(NotFoundDataType.ACCOUNT_ID, "계좌번호를 찾을 수 없습니다.")
    }

    val account = accountsRepository.findById(command.accountId).get()

    if (account.totalAmount != BigDecimal.ZERO) {
      throw InvalidAmountException("삭제 요청하신 계좌에 잔고가 존재합니다. 잔고를 출금 혹은 이체해주세요.")
    }

    account.setDelete(command.requester)
    accountsRepository.save(account)
  }
}
