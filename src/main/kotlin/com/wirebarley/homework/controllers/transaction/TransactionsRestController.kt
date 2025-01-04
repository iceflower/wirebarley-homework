package com.wirebarley.homework.controllers.transaction

import com.wirebarley.homework.controllers.account.request.RemoveAccountRequest
import com.wirebarley.homework.controllers.transaction.request.DepositRequest
import com.wirebarley.homework.controllers.transaction.request.TransferRequest
import com.wirebarley.homework.controllers.transaction.request.WithdrawalRequest
import com.wirebarley.homework.controllers.transaction.response.DepositResponse
import com.wirebarley.homework.controllers.transaction.response.TransferResponse
import com.wirebarley.homework.controllers.transaction.response.WithdrawalResponse
import com.wirebarley.homework.services.common.exception.NotFoundDataType
import com.wirebarley.homework.services.common.exception.NotFoundException
import com.wirebarley.homework.services.transaction.deposit.DepositTransactionRegistrar
import com.wirebarley.homework.services.transaction.deposit.command.CreateDepositTransactionCommand
import com.wirebarley.homework.services.transaction.transfer.TransferTransactionRegistrar
import com.wirebarley.homework.services.transaction.transfer.command.CreateTransferTransactionCommand
import com.wirebarley.homework.services.transaction.withdrawal.WithdrawalTransactionRegistrar
import com.wirebarley.homework.services.transaction.withdrawal.command.CreateWithdrawalTransactionCommand
import com.wirebarley.homework.vo.common.TransactionChannel
import com.wirebarley.homework.vo.common.TransactionType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
@RequestMapping("/transaction")
class TransactionsRestController(
  private val depositTransactionRegistrar: DepositTransactionRegistrar,
  private val transferTransactionRegistrar: TransferTransactionRegistrar,
  private val withdrawalTransactionRegistrar: WithdrawalTransactionRegistrar
) {

  /**
   * POST : 입금 요청을 처리합니다.
   *
   * @param transactionChannel 거래 채널
   * @param request 입금요청 객체
   * @return 입금결과 응답
   */
  @PostMapping("/{transactionChannel}/deposit")
  fun deposit(
    @PathVariable(name = "transactionChannel") transactionChannel: String,
    @RequestBody request: DepositRequest
  ): ResponseEntity<DepositResponse> {

    val command = CreateDepositTransactionCommand(
      request.targetAccountId!!,
      BigDecimal(request.amount!!),
      castTransactionChannel(transactionChannel),
      requester = request.email!!
    )

    val statement = depositTransactionRegistrar.addNewDepositTransaction(command)

    return ResponseEntity.ok(
      DepositResponse.ok(statement)
    )
  }

  /**
   * POST : 이체 요청을 처리합니다.
   *
   * @param transactionChannel 거래 채널
   * @param request 이체요청 객체
   * @return 이체결과 응답
   */
  @PostMapping("/{transactionChannel}/transfer")
  fun transfer(
    @PathVariable(name = "transactionChannel") transactionChannel: String,
    @RequestBody request: TransferRequest
  ): ResponseEntity<TransferResponse> {

    val command = CreateTransferTransactionCommand(
      request.originAccountId!!,
      request.targetAccountId!!,
      BigDecimal(request.amount!!),
      castTransactionChannel(transactionChannel),
      requester = request.email!!
    )

    val statement = transferTransactionRegistrar.addNewTransferTransaction(command)

    return ResponseEntity.ok(
      TransferResponse.ok(statement)
    )
  }

  /**
   * POST : 출금 요청을 처리합니다.
   *
   * @param transactionChannel 거래 채널
   * @param request 출금요청 객체
   * @return 출금결과 응답
   */
  @PostMapping("/{transactionChannel}/withdrawal")
  fun withdrawal(
    @PathVariable(name = "transactionChannel") transactionChannel: String,
    @RequestBody request: WithdrawalRequest
  ): ResponseEntity<WithdrawalResponse> {

    val command = CreateWithdrawalTransactionCommand(
      request.originAccountId!!,
      BigDecimal(request.amount!!),
      castTransactionChannel(transactionChannel),
      requester = request.email!!
    )

    val statement = withdrawalTransactionRegistrar.addNewWithdrawalTransaction(command)

    return ResponseEntity.ok(
      WithdrawalResponse.ok(statement)
    )
  }

  /**
   * 문자열 값으로 이루어진 거래채널 값을, 열거형으로 변환하여 돌려줍니다.
   *
   * @param strTransactionChannel 문자열 값으로 이루어진 거래채널 값
   * @return 열거형 값으로 이루어진 거래채널 값
   */
  private fun castTransactionChannel(strTransactionChannel: String): TransactionChannel {
    val set = TransactionType.entries
      .map { it.toString() }
      .toSet()

    if (!set.contains(strTransactionChannel)) {
      throw NotFoundException(NotFoundDataType.TRANSACTION_CHANNEL, "찾을 수 없는 거래유형 입니다.")
    }

    return TransactionChannel.valueOf(strTransactionChannel)

  }
}
