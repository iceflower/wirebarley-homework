package com.wirebarley.homework.services.transfer

import com.wirebarley.homework.PostgresTestContainer
import com.wirebarley.homework.jpa.entities.account.AccountsRepository
import com.wirebarley.homework.jpa.entities.transaction.TransactionsRepository
import com.wirebarley.homework.services.common.exception.InvalidAmountException
import com.wirebarley.homework.services.common.exception.NotFoundException
import com.wirebarley.homework.services.common.exception.RateLimitExceededException
import com.wirebarley.homework.services.deposit.DepositTransactionRegistrar
import com.wirebarley.homework.services.deposit.command.CreateDepositTransactionCommand
import com.wirebarley.homework.services.transfer.command.CreateTransferTransactionCommand
import com.wirebarley.homework.vo.common.TransactionChannel
import com.wirebarley.homework.vo.common.TransactionType
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
import kotlin.test.assertNotEquals

@DataJpaTest
@ActiveProfiles("test")
@PostgresTestContainer
@TestPropertySource(locations = ["classpath:application-test.yml"])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TransferTransactionRegistrarTest {

  @Autowired
  lateinit var accountsRepository: AccountsRepository

  @Autowired
  lateinit var transactionsRepository: TransactionsRepository

  lateinit var transferTransactionRegistrar: TransferTransactionRegistrar

  @BeforeEach
  fun init() {
    transferTransactionRegistrar = TransferTransactionRegistrar(accountsRepository, transactionsRepository)
  }


  @Nested
  @DisplayName("addNewTransferTransaction(CreateTransferTransactionCommand) 메소드는")
  inner class DscribeOf_addNewTransferTransaction_method {

    fun subject(command: CreateTransferTransactionCommand) = transferTransactionRegistrar.addNewTransferTransaction(command)


    @Nested
    @DisplayName("존재하지 않는 출금계좌가 주어지면")
    inner class ContextWith_nonexistence_origin_account {

      @Test
      @DisplayName("NotFoundException 예외를 던진다")
      fun it_throws_exception() {
        assertThrows<NotFoundException> {
          subject(
            CreateTransferTransactionCommand(
              -1,
              38352658567418873,
              BigDecimal(500),
              TransactionChannel.ONLINE,
              "test"
            )
          )
        }
      }
    }


    @Nested
    @DisplayName("존재하지 않는 입금계좌가 주어지면")
    inner class ContextWith_nonexistence_target_account {
      @Test
      @DisplayName("NotFoundException 예외를 던진다")
      fun it_throws_exception() {
        assertThrows<NotFoundException> {
          subject(
            CreateTransferTransactionCommand(
              38352658567418872,
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
            CreateTransferTransactionCommand(
              38352658567418872,
              38352658567418873,
              BigDecimal(500),
              TransactionChannel.ONLINE,
              "test"
            )
          )
        }
      }
    }


    @Nested
    @DisplayName("출금계좌의 일일이체한도가 초과한 상태라면")
    inner class ContextWith_origin_account_transfer_ratelimit_exceeded {
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
            CreateTransferTransactionCommand(
              38352658567418872,
              38352658567418873,
              BigDecimal(5000000),
              TransactionChannel.ONLINE,
              "test"
            )
          )
        }
      }
    }

    @Nested
    @DisplayName("1원 미만의 이체금액이 주어지면")
    inner class ContextWith_amount_is_zero {

      @Test
      @DisplayName("InvalidAmountException 예외를 던진다")
      fun it_throws_exception() {
        assertThrows<InvalidAmountException> {
          subject(
            CreateTransferTransactionCommand(
              38352658567418872,
              38352658567418873,
              BigDecimal(-1),
              TransactionChannel.ONLINE,
              "test"
            )
          )
        }
      }
    }


    @Nested
    @DisplayName("입금계좌, 출금계좌, 잔액, 이체금액, 일일이체한도 등, 이체 가능 조건에 문제 없을 경우")
    inner class ContextWith_all_conditions_valid {

      @BeforeEach
      fun prepare() {
        // 출금계좌에 테스트용 잔고 입금
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
      @DisplayName("출금계좌에서 이체계좌로 이체가 이루어진다")
      fun it_runs_successful() {
        val result = assertDoesNotThrow {
          subject(
            CreateTransferTransactionCommand(
              38352658567418872,
              38352658567418873,
              BigDecimal(500),
              TransactionChannel.ONLINE,
              "test"
            )
          )
        }

        val afterOriginAccount = accountsRepository.findById(38352658567418872).get()
        val afterTargetAccount = accountsRepository.findById(38352658567418873).get()

        assertNotEquals(BigDecimal(1500).setScale(6), afterOriginAccount.totalAmount)
        assertEquals(
          BigDecimal(1500).setScale(6) - (BigDecimal(500) + ((BigDecimal(500) * TransactionType.TRANSFER.feeRatio))),
          afterOriginAccount.totalAmount
        )
        assertEquals(BigDecimal(500).setScale(6), afterTargetAccount.totalAmount)
        assertEquals(38352658567418872, result.originAccountId)
        assertEquals(38352658567418873, result.targetAccountId)
        assertEquals(BigDecimal(500), result.amount)
        assertEquals((BigDecimal(500) * TransactionType.TRANSFER.feeRatio).setScale(1), result.feeAmount)
      }
    }
  }
}
