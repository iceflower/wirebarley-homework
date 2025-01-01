package com.wirebarley.homework.services.withdrawal

import com.wirebarley.homework.PostgresTestContainer
import com.wirebarley.homework.jpa.entities.account.AccountsRepository
import com.wirebarley.homework.jpa.entities.transaction.TransactionsRepository
import com.wirebarley.homework.services.common.exception.InvalidAmountException
import com.wirebarley.homework.services.common.exception.NotFoundException
import com.wirebarley.homework.services.common.exception.RateLimitExceededException
import com.wirebarley.homework.services.deposit.DepositTransactionRegistrar
import com.wirebarley.homework.services.deposit.command.CreateDepositTransactionCommand
import com.wirebarley.homework.services.transfer.command.CreateTransferTransactionCommand
import com.wirebarley.homework.services.withdrawal.command.CreateWithdrawalTransactionCommand
import com.wirebarley.homework.vo.common.TransactionChannel
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import java.math.BigDecimal
import kotlin.test.Test

@DataJpaTest
@ActiveProfiles("test")
@PostgresTestContainer
@TestPropertySource(locations = ["classpath:application-test.yml"])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class WithdrawalTransactionRegistrarTest {
  @Autowired
  lateinit var accountsRepository: AccountsRepository

  @Autowired
  lateinit var transactionsRepository: TransactionsRepository

  lateinit var withdrawalTransactionRegistrar: WithdrawalTransactionRegistrar

  @BeforeEach
  fun init() {
    withdrawalTransactionRegistrar = WithdrawalTransactionRegistrar(accountsRepository, transactionsRepository)
  }


  @Nested
  @DisplayName("addNewWithdrawalTransaction(CreateWithdrawalTransactionCommand) 메소드는")
  inner class DscribeOf_addNewTransferTransaction_method {

    fun subject(command: CreateWithdrawalTransactionCommand) = withdrawalTransactionRegistrar.addNewWithdrawalTransaction(command)

    @Nested
    @DisplayName("존재하지 않는 출금계좌가 주어지면")
    inner class ContextWith_nonexistence_origin_account {
      @Test
      @DisplayName("NotFoundException 예외를 던진다")
      fun it_throws_exception() {
        assertThrows<NotFoundException> {
          subject(
            CreateWithdrawalTransactionCommand(
             -1,
              BigDecimal(500),
              TransactionChannel.ONLINE,
              "test"
            )
          )
        }
      }
    }

    @Nested
    @DisplayName("출금계좌의 잔고가 부족하면")
    inner class ContextWith_insufficient_balance_origin_account {

      @Test
      @DisplayName("InvalidAmountException 예외를 던진다")
      fun it_throws_exception() {
        assertThrows<InvalidAmountException> {
          subject(
            CreateWithdrawalTransactionCommand(
              38352658567418872,
              BigDecimal(5000),
              TransactionChannel.ONLINE,
              "test"
            )
          )
        }
      }
    }


    @Nested
    @DisplayName("1원 미만의 출금액이 주어지면")
    inner class ContextWith_amount_is_zero {

      @Test
      @DisplayName("InvalidAmountException 예외를 던진다")
      fun it_throws_exception() {
        assertThrows<InvalidAmountException> {
          subject(
            CreateWithdrawalTransactionCommand(
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
    @DisplayName("출금계좌의 일일출금한도가 초과한 상태라면")
    inner class ContextWith_origin_account_withdrawal_ratelimit_exceeded {
      @BeforeEach
      fun prepare() {
        // 출금계좌에 테스트용 잔고 입금
        val depositTransactionRegistrar = DepositTransactionRegistrar(accountsRepository, transactionsRepository)
        depositTransactionRegistrar.addNewDepositTransaction(
          CreateDepositTransactionCommand(
            38352658567418872,
            BigDecimal(1000000000),
            TransactionChannel.BANK_TELLER,
            "test"
          )
        )
      }

      @Test
      @DisplayName("RateLimitExceededException 예외를 던진다")
      fun it_throws_exception() {
        assertThrows<RateLimitExceededException> {
          subject(
            CreateWithdrawalTransactionCommand(
              38352658567418872,
              BigDecimal(5000000),
              TransactionChannel.ONLINE,
              "test"
            )
          )
        }
      }
    }
  }
}
