package com.wirebarley.homework.services.account

import com.wirebarley.homework.PostgresTestContainer
import com.wirebarley.homework.jpa.entities.account.AccountsRepository
import com.wirebarley.homework.jpa.entities.transaction.TransactionsRepository
import com.wirebarley.homework.services.account.command.RemoveAccountCommand
import com.wirebarley.homework.services.common.exception.InvalidAmountException
import com.wirebarley.homework.services.common.exception.NotFoundException
import com.wirebarley.homework.services.deposit.DepositTransactionRegistrar
import com.wirebarley.homework.services.deposit.command.CreateDepositTransactionCommand
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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@DataJpaTest
@ActiveProfiles("test")
@PostgresTestContainer
@TestPropertySource(locations = ["classpath:application-test.yml"])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("AccountRemover 클래스의")
class AccountRemoverTest {

  @Autowired
  lateinit var accountsRepository: AccountsRepository

  @Autowired
  lateinit var transactionsRepository: TransactionsRepository

  lateinit var accountRemover: AccountRemover

  @BeforeEach
  fun init() {
    accountRemover = AccountRemover(accountsRepository)
  }

  @Nested
  @DisplayName("remove(RemoveAccountCommand) 메소드는")
  inner class DescribeOf_register_method {

    fun subject(command: RemoveAccountCommand) = accountRemover.remove(command)

    @Nested
    @DisplayName("존재하지 않는 계좌번호가 주어지면")
    inner class ContextWith_nonExist_account_id {

      @Test
      @DisplayName("NotFoundException 예외를 던진다")
      fun it_throws_exception() {
        assertThrows<NotFoundException> {
          subject(
            RemoveAccountCommand(
              -1,
              "test"
            )
          )
        }
      }
    }

    @Nested
    @DisplayName("잔고가 1원 이상인 계좌번호가 주어지면")
    inner class ContextWith_exist_email {

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
      @DisplayName("InvalidAmountException 예외를 던진다")
      fun it_throws_exception() {
        assertThrows<InvalidAmountException> {
          subject(
            RemoveAccountCommand(
              38352658567418872,
              "test"
            )
          )
        }
      }
    }


    @Nested
    @DisplayName("잔고가 0원인 계좌번호가 주어지면")
    inner class ContextWith_valid_accountId {

      @Test
      @DisplayName("잔고 삭제에 성공한다")
      fun it_runs_successfully() {
        assertDoesNotThrow {
          subject(
            RemoveAccountCommand(
              38352658567418872,
              "test"
            )
          )
        }

        assertTrue { accountsRepository.existsById(38352658567418872) }
        assertFalse { accountsRepository.findById(38352658567418872).get().isActive }
      }
    }
  }
}
