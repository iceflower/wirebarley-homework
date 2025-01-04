package com.wirebarley.homework.services.transaction.deposit

import com.wirebarley.homework.PostgresTestContainer
import com.wirebarley.homework.jpa.entities.account.AccountsRepository
import com.wirebarley.homework.jpa.entities.transaction.TransactionsRepository
import com.wirebarley.homework.services.common.exception.InvalidAmountException
import com.wirebarley.homework.services.common.exception.NotFoundException
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
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

@DataJpaTest
@ActiveProfiles("test")
@PostgresTestContainer
@TestPropertySource(locations = ["classpath:application-test.yml"])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("DepositTransactionRegistrar 클래스의")
class DepositTransactionRegistrarTest {

  @Autowired
  lateinit var accountsRepository: AccountsRepository

  @Autowired
  lateinit var transactionsRepository: TransactionsRepository

  lateinit var depositTransactionRegistrar: DepositTransactionRegistrar

  @BeforeEach
  fun init() {
    depositTransactionRegistrar = DepositTransactionRegistrar(accountsRepository, transactionsRepository)
  }


  @Nested
  @DisplayName("addNewDepositTransaction(CreateDepositTransactionCommand) 메소드는")
  inner class DscribeOf_addNewDepositTransaction_method {

    fun subject(command: CreateDepositTransactionCommand) = depositTransactionRegistrar.addNewDepositTransaction(command)


    @Nested
    @DisplayName("존재하지 않는 입금계좌가 주어지면")
    inner class ContextWith_nonexistence_target_account {
      @Test
      @DisplayName("NotFoundException 예외를 던진다")
      fun it_throws_exception() {
        assertThrows<NotFoundException> {
          subject(
            CreateDepositTransactionCommand(
              -1,
              BigDecimal(100),
              TransactionChannel.ONLINE,
              "test"
            )
          )
        }
      }
    }

    @Nested
    @DisplayName("1원 미만의 입금액이 주어지면")
    inner class ContextWith_amount_is_zero {

      @Test
      @DisplayName("InvalidAmountException 예외를 던진다")
      fun it_throws_exception() {
        assertThrows<InvalidAmountException> {
          subject(
            CreateDepositTransactionCommand(
              38352658567418872,
              BigDecimal(-1),
              TransactionChannel.ONLINE,
              "test"
            )
          )
        }
      }
    }

    @Nested
    @DisplayName("입금계좌, 입금액 등 입금 가능 조건에 문제 없을 경우")
    inner class ContextWith_all_conditions_valid {

      @Test
      @DisplayName("입금계좌에 입금이 이루어진다")
      fun it_runs_successful() {
        val result = assertDoesNotThrow {
          subject(
            CreateDepositTransactionCommand(
              38352658567418872,
              BigDecimal(5000),
              TransactionChannel.ONLINE,
              "test"
            )
          )
        }

        assertEquals(38352658567418872, result.targetAccountId)
        assertEquals(BigDecimal(5000), result.amount)
        assertEquals(BigDecimal.ZERO.setScale(1), result.feeAmount)

        val afterTargetAccount = accountsRepository.findById(38352658567418872).get()
        assertEquals(BigDecimal(5000).setScale(6), afterTargetAccount.totalAmount)
      }
    }
  }
}
