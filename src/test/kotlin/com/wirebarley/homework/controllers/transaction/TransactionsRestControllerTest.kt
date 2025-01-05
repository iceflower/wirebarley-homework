package com.wirebarley.homework.controllers.transaction

import com.fasterxml.jackson.databind.ObjectMapper
import com.wirebarley.homework.IntegrateTestContainer
import com.wirebarley.homework.services.transaction.deposit.DepositTransactionRegistrar
import com.wirebarley.homework.services.transaction.deposit.command.CreateDepositTransactionCommand
import com.wirebarley.homework.vo.common.TransactionChannel
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import kotlin.test.Test


@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = ["classpath:application-test.yml"])
@AutoConfigureMockMvc
@IntegrateTestContainer
@DisplayName("거래 API - (/transaction)")
class TransactionsRestControllerTest {

  @Autowired
  lateinit var mockMvc: MockMvc

  @Autowired
  lateinit var depositTransactionRegistrar: DepositTransactionRegistrar

  val objectMapper = ObjectMapper()

  @Nested
  @DisplayName("POST - 입금 api (/transaction/{transactionChannel}/deposit)")
  inner class Describe_deposit_api {

    @Nested
    @DisplayName("존재하지 않는 거래채널이 주어지면")
    inner class ContextWith_nonexist_transactionChannel {

      @Test
      @DisplayName("400 오류와 함께 입금 실패사유를 응답한다")
      fun it_returns_400() {

        val requestBody = objectMapper.writeValueAsString(
          mapOf(
            "targetAccountId" to 38352658567418872,
            "amount" to 100,
            "email" to "test12@test.com"
          )
        )

        mockMvc.perform(
          post("/transaction/{transactionChannel}/deposit", "NOT-EXIST-CHANNEL")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(requestBody)
        )
          .andExpect(status().isBadRequest)
      }
    }

    @Nested
    @DisplayName("존재하지 않는 입금계좌가 주어지면")
    inner class ContextWith_nonexist_targetAccountId {

      @Test
      @DisplayName("400 오류와 함께 입금 실패사유를 응답한다")
      fun it_returns_400() {

        val requestBody = objectMapper.writeValueAsString(
          mapOf(
            "targetAccountId" to 999,
            "amount" to 100,
            "email" to "test12@test.com"
          )
        )

        mockMvc.perform(
          post("/transaction/{transactionChannel}/deposit", "AUTOMATED_TELLER_MACHINE")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(requestBody)
        )
          .andExpect(status().isBadRequest)
      }

    }

    @Nested
    @DisplayName("문제가 없는 요청이 주어지면")
    inner class ContextWith_valid_request {

      @Test
      @DisplayName("200 코드와 함께 입금결과를 응답한다")
      fun it_returns_200() {

        val requestBody = objectMapper.writeValueAsString(
          mapOf(
            "targetAccountId" to 38352658567418872,
            "amount" to 100,
            "email" to "test12@test.com"
          )
        )

        mockMvc.perform(
          post("/transaction/{transactionChannel}/deposit", "AUTOMATED_TELLER_MACHINE")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(requestBody)
        )
          .andExpect(status().isOk)
      }
    }
  }

  @Nested
  @DisplayName("POST - 출금 api (/transaction/{transactionChannel}/withdrawal)")
  inner class Describe_withdrawal_api {

    @Nested
    @DisplayName("존재하지 않는 거래채널이 주어지면")
    inner class ContextWith_nonexist_transactionChannel {

      @Test
      @DisplayName("400 오류와 함께 출금 실패사유를 응답한다")
      fun it_returns_400() {

        val requestBody = objectMapper.writeValueAsString(
          mapOf(
            "originAccountId" to 38352658567418872,
            "amount" to 100,
            "email" to "test12@test.com"
          )
        )

        mockMvc.perform(
          post("/transaction/{transactionChannel}/withdrawal", "NOT-EXIST-CHANNEL")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(requestBody)
        )
          .andExpect(status().isBadRequest)
      }
    }

    @Nested
    @DisplayName("존재하지 않는 계좌번호가 주어지면")
    inner class ContextWith_nonexist_account {

      @Test
      @DisplayName("400 오류와 함께 입금 실패사유를 응답한다")
      fun it_returns_400() {

        val requestBody = objectMapper.writeValueAsString(
          mapOf(
            "originAccountId" to 999,
            "amount" to 100,
            "email" to "test12@test.com"
          )
        )

        mockMvc.perform(
          post("/transaction/{transactionChannel}/withdrawal", "AUTOMATED_TELLER_MACHINE")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(requestBody)
        )
          .andExpect(status().isBadRequest)
      }
    }

    @Nested
    @DisplayName("잔고가 없는 계좌가 주어지면")
    inner class ContextWith_empty_accountId {

      @Test
      @DisplayName("400 오류와 함께 출금 실패사유를 응답한다")
      fun it_returns_400() {

        val requestBody = objectMapper.writeValueAsString(
          mapOf(
            "originAccountId" to 38352658567418872,
            "amount" to 10000000,
            "email" to "test12@test.com"
          )
        )

        mockMvc.perform(
          post("/transaction/{transactionChannel}/withdrawal", "AUTOMATED_TELLER_MACHINE")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(requestBody)
        )
          .andExpect(status().isBadRequest)
      }
    }

    @Nested
    @DisplayName("문제가 없는 요청이 주어지면")
    inner class ContextWith_valid_accountId {

      @BeforeEach
      fun prepare() {
        // 계좌에 테스트용 잔고 입금
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
      @DisplayName("200 코드와 함께 출금 결과 내역을 응답한다")
      fun it_returns_400() {

        val requestBody = objectMapper.writeValueAsString(
          mapOf(
            "originAccountId" to 38352658567418872,
            "amount" to 10000000,
            "email" to "test12@test.com"
          )
        )

        mockMvc.perform(
          post("/transaction/{transactionChannel}/withdrawal", "AUTOMATED_TELLER_MACHINE")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(requestBody)
        )
          .andExpect(status().isBadRequest)
      }
    }
  }

  @Nested
  @DisplayName("POST - 이체 api (/transaction/{transactionChannel}/transfer)")
  inner class Describe_transfer_api {
    @Nested
    @DisplayName("존재하지 않는 거래채널이 주어지면")
    inner class ContextWith_nonexist_transactionChannel {

      @Test
      @DisplayName("400 오류와 함께 출금 실패사유를 응답한다")
      fun it_returns_400() {

        val requestBody = objectMapper.writeValueAsString(
          mapOf(
            "originAccountId" to 38352658567418872,
            "targetAccountId" to 38352658567418873,
            "amount" to 100,
            "email" to "test12@test.com"
          )
        )

        mockMvc.perform(
          post("/transaction/{transactionChannel}/transfer", "NOT-EXIST-CHANNEL")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(requestBody)
        )
          .andExpect(status().isBadRequest)
      }
    }

    @Nested
    @DisplayName("존재하지 않는 출금계좌번호가 주어지면")
    inner class ContextWith_nonexist_account {

      @Test
      @DisplayName("400 오류와 함께 입금 실패사유를 응답한다")
      fun it_returns_400() {

        val requestBody = objectMapper.writeValueAsString(
          mapOf(
            "originAccountId" to 999,
            "targetAccountId" to 38352658567418873,
            "amount" to 100,
            "email" to "test12@test.com"
          )
        )

        mockMvc.perform(
          post("/transaction/{transactionChannel}/transfer", "AUTOMATED_TELLER_MACHINE")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(requestBody)
        )
          .andExpect(status().isBadRequest)
      }
    }

    @Nested
    @DisplayName("존재하지 않는 입금계좌번호가 주어지면")
    inner class ContextWith_nonexist_account2 {

      @Test
      @DisplayName("400 오류와 함께 입금 실패사유를 응답한다")
      fun it_returns_400() {

        val requestBody = objectMapper.writeValueAsString(
          mapOf(
            "originAccountId" to 38352658567418872,
            "targetAccountId" to 999,
            "amount" to 100,
            "email" to "test12@test.com"
          )
        )

        mockMvc.perform(
          post("/transaction/{transactionChannel}/transfer", "AUTOMATED_TELLER_MACHINE")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(requestBody)
        )
          .andExpect(status().isBadRequest)
      }
    }

    @Nested
    @DisplayName("잔고가 없는 출금 계좌번호가 주어지면")
    inner class ContextWith_empty_accountId {

      @Test
      @DisplayName("400 오류와 함께 출금 실패사유를 응답한다")
      fun it_returns_400() {

        val requestBody = objectMapper.writeValueAsString(
          mapOf(
            "originAccountId" to 38352658567418872,
            "targetAccountId" to 38352658567418873,
            "amount" to 10000000,
            "email" to "test12@test.com"
          )
        )

        mockMvc.perform(
          post("/transaction/{transactionChannel}/withdrawal", "AUTOMATED_TELLER_MACHINE")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(requestBody)
        )
          .andExpect(status().isBadRequest)
      }
    }

    @Nested
    @DisplayName("문제가 없는 요청이 주어지면")
    inner class ContextWith_valid_request {

      @BeforeEach
      fun prepare() {
        // 계좌에 테스트용 잔고 입금
        depositTransactionRegistrar.addNewDepositTransaction(
          CreateDepositTransactionCommand(
            38352658567418872,
            BigDecimal(100000000000),
            TransactionChannel.BANK_TELLER,
            "test"
          )
        )
      }

      @Test
      @DisplayName("200 코드와 함께 출금 결과 내역을 응답한다")
      fun it_returns_400() {

        val requestBody = objectMapper.writeValueAsString(
          mapOf(
            "originAccountId" to 38352658567418872,
            "targetAccountId" to 38352658567418873,
            "amount" to 10000000,
            "email" to "test12@test.com"
          )
        )

        mockMvc.perform(
          post("/transaction/{transactionChannel}/withdrawal", "AUTOMATED_TELLER_MACHINE")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(requestBody)
        )
          .andExpect(status().isBadRequest)
      }
    }


  }
}
