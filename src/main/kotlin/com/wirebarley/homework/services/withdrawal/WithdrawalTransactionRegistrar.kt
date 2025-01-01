package com.wirebarley.homework.services.withdrawal

import com.wirebarley.homework.jpa.entities.account.AccountsRepository
import com.wirebarley.homework.jpa.entities.transaction.Transactions
import com.wirebarley.homework.jpa.entities.transaction.TransactionsRepository
import com.wirebarley.homework.services.common.exception.InvalidAmountException
import com.wirebarley.homework.services.common.exception.NotFoundDataType
import com.wirebarley.homework.services.common.exception.NotFoundException
import com.wirebarley.homework.services.common.exception.RateLimitExceededException
import com.wirebarley.homework.services.transfer.command.CreateTransferTransactionCommand
import com.wirebarley.homework.services.withdrawal.command.CreateWithdrawalTransactionCommand
import com.wirebarley.homework.vo.common.TransactionType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class WithdrawalTransactionRegistrar(
  private val accountsRepository: AccountsRepository,
  private val transactionsRepository: TransactionsRepository
) {

  /**
   * 새로운 출금거래를 추가합니다.
   *
   * @param command 이체 거래 생성 명령서
   */
  @Transactional
  fun addNewWithdrawalTransaction(command: CreateWithdrawalTransactionCommand) {

    val originAccountIdExists = accountsRepository.existsById(command.originAccountId)

    if (!originAccountIdExists) {
      throw NotFoundException(NotFoundDataType.ACCOUNT_ID, "존재하지 않는 출금 계좌번호입니다.")
    }


    if (command.amount < BigDecimal.ONE) {
      throw InvalidAmountException("이체금액은 1 이상의 숫자만 입력이 가능합니다.")
    }


    val originAccount = accountsRepository.findById(command.originAccountId).get()
    // 이체 진행시, 출금게좌의 잔액이 음수가 된다면 예외 발생
    val feeAmount = command.amount * TransactionType.WITHDRAWAL.feeRatio
    if ((originAccount.totalAmount - (command.amount + feeAmount)) < BigDecimal.ZERO) {
      throw InvalidAmountException(
        "출금 계좌 잔고가 부족합니다. (출금계좌 잔고: ${originAccount.totalAmount}원, 수수료 포함 출금금액: ${(command.amount + feeAmount)})")
    }

    // 이체 진행시, 출금계좌의 일일이체한도를 넘기게 된다면 예외 발생
    val transferAmountsOfTheDay = BigDecimal(transactionsRepository.sumTransferAmountsOfTheDay(command.originAccountId) ?: 0)
    if ((transferAmountsOfTheDay + (command.amount + feeAmount)) > BigDecimal(TransactionType.TRANSFER.amountLimit)) {
      throw RateLimitExceededException(TransactionType.TRANSFER, "일일 출금한도를 초과하여, 출금을 진행할 수 없습니다.")
    }


    // 이체거래 등록
    val depositTransaction = Transactions.makeNewWithdrawalTransaction(
      command.transactionChannel,
      originAccount,
      command.amount,
      command.requester
    )

    transactionsRepository.save(depositTransaction)

    // 출금계좌 잔액 차감
    originAccount.correctAmount(originAccount.totalAmount - feeAmount, command.requester)
    accountsRepository.save(originAccount)

  }
}
