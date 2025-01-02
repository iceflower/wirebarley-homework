package com.wirebarley.homework.services.account.command

/**
 * 계좌 삭제 명령서 객체
 *
 * @property accountId 삭제대상 계좌번호
 * @property requester 요청자
 */
data class RemoveAccountCommand(val accountId: Long, val requester: String)
