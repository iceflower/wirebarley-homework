package com.wirebarley.homework.controllers.account

import com.wirebarley.homework.controllers.account.request.CreateNewAccountRequest
import com.wirebarley.homework.controllers.account.request.RemoveAccountRequest
import com.wirebarley.homework.controllers.account.response.CreateNewAccountResponse
import com.wirebarley.homework.controllers.account.response.RemoveAccountResponse
import com.wirebarley.homework.controllers.account.response.TransactionsResponse
import com.wirebarley.homework.services.account.AccountRegistrar
import com.wirebarley.homework.services.account.AccountRemover
import com.wirebarley.homework.services.account.AccountTransactionsInformant
import com.wirebarley.homework.services.account.command.CreateNewAccountCommand
import com.wirebarley.homework.services.account.command.RemoveAccountCommand
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/account")
class AccountsRestController(
  private val accountRegistrar: AccountRegistrar,
  private val accountRemover: AccountRemover,
  private val accountTransactionsInformant: AccountTransactionsInformant
) {

  /**
   * POST: 계좌 신규 생성 요청을 처리합니다.
   *
   * @param request 계좌 신규 생성 요청서 객체
   *
   * @return 계좌 생성 결과 응답
   */
  @PostMapping
  fun addNewAccount(@RequestBody request: CreateNewAccountRequest): ResponseEntity<CreateNewAccountResponse> {

    val command = CreateNewAccountCommand(
      request.name!!,
      request.phoneNumber!!,
      request.email!!,
      requester = request.email
    )

    val response = accountRegistrar.register(command)

    return ResponseEntity.ok(CreateNewAccountResponse.ok(response))
  }

  /**
   * DELETE: 계좌 삭제 요청을 처리합니다.
   *
   * @param request 계좌 삭제 요청서 객체
   * @return 계좌 삭제 결과 응답
   */
  @DeleteMapping
  fun removeAccount(
    @RequestBody request: RemoveAccountRequest
  ): ResponseEntity<RemoveAccountResponse> {

    val command = RemoveAccountCommand(
      request.accountId!!,
      request.email!!
    )

    val response = accountRemover.remove(command)
    return ResponseEntity.ok(
      RemoveAccountResponse.ok(response)
    )
  }

  /**
   * GET: 계좌별 거래내역 조회 요청을 처리합니다.
   *
   * @param accountId 조회대상 계좌번호
   * @param pageNumber 페이지 번호 (optional, 기본값 0)
   * @param pageSize 페이지 사이즈 (optional, 기본값 30)
   * @return 계좌 생성 결과 리스트 응답
   */

  @GetMapping("/{accountId}/transactions")
  fun getTransactions(
    @PathVariable(name = "accountId") accountId: Long,
    @RequestParam("pageNumber", required = false, defaultValue = "0") pageNumber: Int,
    @RequestParam("pageSize", required = false, defaultValue = "30") pageSize: Int
  ): ResponseEntity<TransactionsResponse> {

    val pagedTransactions = accountTransactionsInformant.transactions(accountId, PageRequest.of(pageNumber, pageSize))
    return ResponseEntity.ok(
      TransactionsResponse.ok(pagedTransactions)
    )
  }
}
