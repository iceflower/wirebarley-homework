package com.wirebarley.homework.controllers.account

import com.fasterxml.jackson.databind.ObjectMapper
import com.wirebarley.homework.IntegrateTestContainer
import com.wirebarley.homework.services.transaction.deposit.DepositTransactionRegistrar
import com.wirebarley.homework.services.transaction.deposit.command.CreateDepositTransactionCommand
import com.wirebarley.homework.vo.common.TransactionChannel
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
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
@DisplayName("계좌 API - (/account)")
class AccountsRestControllerTest {

  @Autowired
  lateinit var mockMvc: MockMvc

  @Autowired
  lateinit var depositTransactionRegistrar: DepositTransactionRegistrar

  val objectMapper = ObjectMapper()

  @Nested
  @DisplayName("POST - 신규계좌 추가")
  inner class DescribeOf_create_new_account_api {

    @Nested
    @DisplayName("중복된 전화번호 혹은 중복된 이메일 주소가 담긴 요청이 주어지면")
    inner class ContextWith_duplicatedInformation {

      @CsvSource(
        value = [
          "테스트1:010-123-1234:test9999@test.com",
          "테스트2:010-9999-9999:test1@test.com"
        ],
        delimiter = ':'
      )
      @ParameterizedTest
      @DisplayName("400 오류와 함께 계좌 생성 실패 사유를 응답한다")
      fun it_returns_400(name: String, phoneNumber: String, email: String) {
        val requestBody = objectMapper.writeValueAsString(
          mapOf(
            "name" to name,
            "phoneNumber" to phoneNumber,
            "email" to email
          )
        )
        mockMvc.perform(
          post("/account")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(requestBody)
        )
          .andExpect(status().isBadRequest)
      }
    }

    @Nested
    @DisplayName("정상적인 요청이 주어지면")
    inner class ContextWith_request {

      @Test
      @DisplayName("계좌 생성에 성공하고, 200 코드와 함께 코드 생성 결과 명세를 반환한다")
      fun it_returns_200() {
        val requestBody = objectMapper.writeValueAsString(
          mapOf(
            "name" to "신규고객1",
            "phoneNumber" to "010-9999-9999",
            "email" to "test9999@test.com"
          )
        )
        mockMvc.perform(
          post("/account")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(requestBody)
        )
          .andExpect(status().isOk)
      }
    }
  }

  @Nested
  @DisplayName("DELETE - 기존계좌 삭제 API")
  inner class DescribeOf_delete_api {

    @Nested
    @DisplayName("존재하지 않는 계좌번호가 주어지면")
    inner class ContextWith_nonExist_accountId {
      @Test
      @DisplayName("400 오류와 함께 계좌 생성 실패 사유를 응답한다")
      fun it_returns_400() {

        val requestBody = objectMapper.writeValueAsString(
          mapOf(
            "accountId" to -1,
            "email" to "test@test.com"
          )
        )
        mockMvc.perform(
          delete("/account")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(requestBody)
        )
          .andExpect(status().isBadRequest)
      }
    }

    @Nested
    @DisplayName("잔고가 존재하는 계좌번호가 주어지면")
    inner class ContextWith_notEmpty_accountId {
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
      @DisplayName("400 오류와 함께 계좌 생성 실패 사유를 응답한다")
      fun it_returns_400() {

        val requestBody = objectMapper.writeValueAsString(
          mapOf(
            "accountId" to 38352658567418872,
            "email" to "test@test.com"
          )
        )
        mockMvc.perform(
          delete("/account")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(requestBody)
        )
          .andExpect(status().isBadRequest)
      }
    }

    @Nested
    @DisplayName("삭제 요청에 문제가 없으면")
    inner class ContextWith_valid_request {

      @Test
      @DisplayName("계좌 삭제에 성공한 후, 200 코드와 함께 계좌 삭제 결과를 응답한다")
      fun it_returns_200() {

        val requestBody = objectMapper.writeValueAsString(
          mapOf(
            "accountId" to 38352658567418873,
            "email" to "test@test.com"
          )
        )
        mockMvc.perform(
          delete("/account")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(requestBody)
        )
          .andExpect(status().isOk)
      }
    }
  }

  @Nested
  @DisplayName("GET - 계좌 거래내역 API")
  inner class DescribeOf_get_transactions_list_api {

    @Nested
    @DisplayName("존재하지 않는 계좌번호가 주어지면")
    inner class ContextWith_nonexist_accountId {

      @Test
      @DisplayName("400 오류와 함께 조회 실패사유를 응답한다")
      fun it_returns_400() {
        mockMvc.perform(
          get("/account/{accountId}/transactions", 0)
        )
          .andExpect(status().isBadRequest)
      }
    }

    @Nested
    @DisplayName("존재하지 않는 계좌번호가 주어지면")
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
      @DisplayName("200 코드와 함께 거래내역을 응답한다")
      fun it_returns_400() {
        mockMvc.perform(
          get("/account/{accountId}/transactions", 38352658567418872)
        )
          .andExpect(status().isOk)
      }
    }
  }
}
