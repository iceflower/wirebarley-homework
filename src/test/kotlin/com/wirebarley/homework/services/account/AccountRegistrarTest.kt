package com.wirebarley.homework.services.account

import com.wirebarley.homework.PostgresTestContainer
import com.wirebarley.homework.jpa.entities.account.AccountsRepository
import com.wirebarley.homework.services.account.command.CreateNewAccountCommand
import com.wirebarley.homework.services.common.exception.AlreadyExistException
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
import kotlin.test.Test

@DataJpaTest
@ActiveProfiles("test")
@PostgresTestContainer
@TestPropertySource(locations = ["classpath:application-test.yml"])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("AccountRegistrar 클래스의")
class AccountRegistrarTest {

  @Autowired
  lateinit var accountsRepository: AccountsRepository

  lateinit var accountRegistrar: AccountRegistrar

  @BeforeEach
  fun init() {
    accountRegistrar = AccountRegistrar(accountsRepository)
  }

  @Nested
  @DisplayName("register(CreateNewAccountCommand) 메소드는")
  inner class DescribeOf_register_method {

    fun subject(command: CreateNewAccountCommand) = accountRegistrar.register(command)

    @Nested
    @DisplayName("이미 존재하는 전화번호가 주어지면")
    inner class ContextWith_exist_phone_number {
      @Test
      @DisplayName("AlreadyExistException 예외를 던진다")
      fun it_throws_exception() {
        assertThrows<AlreadyExistException> {
          subject(
            CreateNewAccountCommand(
              "테스트",
              "010-123-1234",
              "test9999@test.com",
              "test"
            )
          )
        }
      }
    }

    @Nested
    @DisplayName("이미 존재하는 이메일이 주어지면")
    inner class ContextWith_exist_email {

      @Test
      @DisplayName("AlreadyExistException 예외를 던진다")
      fun it_throws_exception() {
        assertThrows<AlreadyExistException> {
          subject(
            CreateNewAccountCommand(
              "테스트",
              "010-9999-9999",
              "test1@test.com",
              "test"
            )
          )
        }
      }
    }

    @Nested
    @DisplayName("존재하지 않는 전화번호와 이메일이 주어지면")
    inner class ContextWith_valid_phoneNumber_and_email {
      @Test
      @DisplayName("계좌 생성에 성공한다")
      fun it_runs_successfully() {
        assertDoesNotThrow  {
          subject(
            CreateNewAccountCommand(
              "테스트",
              "010-9999-9999",
              "test99999@test.com",
              "test"
            )
          )
        }
      }
    }
  }
}
