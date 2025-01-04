package com.wirebarley.homework.services.account

import com.wirebarley.homework.jpa.entities.account.Accounts
import com.wirebarley.homework.jpa.entities.account.AccountsRepository
import com.wirebarley.homework.services.account.command.CreateNewAccountCommand
import com.wirebarley.homework.services.account.statememt.RegisteredAccountStatement
import com.wirebarley.homework.services.common.exception.AlreadyExistException
import com.wirebarley.homework.services.common.exception.ExistDataType
import com.wirebarley.homework.util.lock.distributed.RedisDistributedLock
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class AccountRegistrar(private val accountsRepository: AccountsRepository) {

  /**
   * 계좌를 신규 개설합니다.
   *
   * @param command 계좌 신규개설 명령서
   * @return 계좌 생성 결과 명세
   */
  @RedisDistributedLock(key = "#create-new-account", timeUnit = TimeUnit.MICROSECONDS, waitTime = 100L, leaseTime = 100L)
  fun register(command: CreateNewAccountCommand): RegisteredAccountStatement {
    val phoneNumberExists = accountsRepository.existsByOwnerPhoneNumber(command.phoneNumber)

    if (phoneNumberExists) {
      throw AlreadyExistException(ExistDataType.PHONE_NUMBER, "이미 사용중인 전화번호입니다.")
    }

    val emailExists = accountsRepository.existsByOwnerEmail(command.email)

    if (emailExists) {
      throw AlreadyExistException(ExistDataType.EMAIL, "이미 사용중인 이메일 주소입니다.")
    }

    val newAccount = Accounts.createNewAccount(
      command.name,
      command.phoneNumber,
      command.email,
      command.requester
    )

    val savedNewAccount = accountsRepository.save(newAccount)

    return RegisteredAccountStatement(savedNewAccount.id!!, savedNewAccount.audit.updatedAt)
  }
}
