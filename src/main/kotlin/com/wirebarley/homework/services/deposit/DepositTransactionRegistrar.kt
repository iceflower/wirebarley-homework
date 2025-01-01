package com.wirebarley.homework.services.deposit

import com.wirebarley.homework.jpa.entities.account.AccountsRepository
import com.wirebarley.homework.jpa.entities.transaction.Transactions
import com.wirebarley.homework.jpa.entities.transaction.TransactionsRepository
import com.wirebarley.homework.services.common.exception.InvalidAmountException
import com.wirebarley.homework.services.common.exception.NotFoundDataType
import com.wirebarley.homework.services.common.exception.NotFoundException
import com.wirebarley.homework.services.deposit.command.CreateDepositTransactionCommand
import com.wirebarley.homework.util.lock.distributed.RedisDistributedLock
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class DepositTransactionRegistrar(
  private val accountsRepository: AccountsRepository,
  private val transactionsRepository: TransactionsRepository
) {

  /**
   * 새로운 입금 거래를 추가합니다.
   *
   * @param command 입금 거래 생성 명령서
   */
  @RedisDistributedLock(key = "#deposit")
  fun addNewDepositTransaction(command: CreateDepositTransactionCommand) {
    val targetAccountIdExists = accountsRepository.existsById(command.targetAccountId)

    if (!targetAccountIdExists) {
      throw NotFoundException(NotFoundDataType.ACCOUNT_ID, "존재하지 않는 계좌번호입니다.")
    }

    if (command.amount < BigDecimal.ONE) {
      throw InvalidAmountException("입금액은 1 이상의 숫자만 입력이 가능합니다.")
    }

    val targetAccount = accountsRepository.findById(command.targetAccountId).get()

    // 이체거래 등록
    val depositTransaction = Transactions.makeNewDepositTransaction(
      command.transactionChannel,
      targetAccount,
      command.amount,
      command.requester
    )

    transactionsRepository.save(depositTransaction)

    // 입금계좌 잔액 가산
    targetAccount.correctAmount(targetAccount.totalAmount + command.amount, command.requester)
    accountsRepository.save(targetAccount)
  }
}
