package com.wirebarley.homework.services.account

import com.wirebarley.homework.PostgresTestContainer
import com.wirebarley.homework.jpa.entities.account.AccountsRepository
import com.wirebarley.homework.jpa.entities.transaction.TransactionsRepository
import com.wirebarley.homework.services.account.command.RemoveAccountCommand
import com.wirebarley.homework.services.common.exception.NotFoundException
import com.wirebarley.homework.services.transaction.deposit.DepositTransactionRegistrar
import com.wirebarley.homework.services.transaction.deposit.command.CreateDepositTransactionCommand
import com.wirebarley.homework.vo.common.TransactionChannel
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@DataJpaTest
@ActiveProfiles("test")
@PostgresTestContainer
@TestPropertySource(locations = ["classpath:application-test.yml"])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("AccountTransactionsInformant 클래스의")
 class AccountTransactionsInformantTest {

  @Autowired
  lateinit var accountsRepository: AccountsRepository

  @Autowired
  lateinit var transactionsRepository: TransactionsRepository

  lateinit var accountTransactionsInformant: AccountTransactionsInformant

  @BeforeEach
  fun init() {
    accountTransactionsInformant = AccountTransactionsInformant(accountsRepository, transactionsRepository)
  }

  @Nested
  @DisplayName("transactions(Long, Pageable) 메소드는")
  inner class DescribeOf_transactions_method {

    fun subject(accountId: Long, pageable: Pageable) = accountTransactionsInformant.transactions(accountId, pageable)

    @Nested
    @DisplayName("존재하지 않는 계좌번호가 주어지면")
    inner class ContextWith_nonexist_accountId {

      @Test
      @DisplayName("NotFoundException 예외를 던진다")
      fun it_throws_exceptions() {
        assertThrows<NotFoundException> {
          subject(-1, PageRequest.of(0, 10))
        }
      }
    }

    @Nested
    @DisplayName("거래내역이 존재하지 않는 계좌번호가 주어지면")
    inner class ContextWith_accountId_has_not_any_transactions {

      @Test
      @DisplayName("비어있는 리스트를 돌려준다")
      fun it_returns_empty_list() {
        val list = assertDoesNotThrow { subject(38352658567418872, PageRequest.of(0, 10)) }
        assertTrue { list.isEmpty }
      }
    }

    @Nested
    @DisplayName("거래내역이 존재하는 계좌번호가 주어지면")
    inner class ContextWith_accountId_has_transactions {

      @BeforeEach
      fun prepare() {
        // 계좌에 테스트용 잔고 입금
        val depositTransactionRegistrar = DepositTransactionRegistrar(accountsRepository, transactionsRepository)
        depositTransactionRegistrar.addNewDepositTransaction(
          CreateDepositTransactionCommand(
            38352658567418872,
            BigDecimal(1500),
            TransactionChannel.BANK_TELLER,
            "test"
          )
        )
      }

      @Test
      @DisplayName("비어있는 리스트를 돌려준다")
      fun it_returns_not_empty_list() {
        val list = assertDoesNotThrow { subject(38352658567418872, PageRequest.of(0, 10)) }
        assertFalse { list.isEmpty }
        assertEquals(1, list.totalPages )
        assertEquals(1, list.totalElements )
      }
    }
  }
 }
