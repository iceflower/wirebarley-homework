package com.wirebarley.homework.jpa.entities.transaction

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface TransactionsRepository : JpaRepository<Transactions, String> {

  @EntityGraph(attributePaths = ["originAccount"])
  @Query(value = "select sum(tr.amount)" +
    "from Transactions tr " +
    "where tr.transactionType = com.wirebarley.homework.vo.common.TransactionType.TRANSFER " +
    "and tr.originAccount.id = :accountId " +
    "and extract(DATE FROM tr.audit.updatedAt) = current_date" )
  fun sumTransferAmountsOfTheDay(@Param("accountId") accountId: Long): Long?

  @EntityGraph(attributePaths = ["originAccount"])
  @Query(value = "select sum(tr.amount)" +
    "from Transactions tr " +
    "where tr.transactionType = com.wirebarley.homework.vo.common.TransactionType.WITHDRAWAL " +
    "and tr.originAccount.id = :accountId " +
    "and extract(DATE FROM tr.audit.updatedAt) = current_date" )
  fun sumWithdrawalAmountsOfTheDay(@Param("accountId") accountId: Long): Long?

  @EntityGraph(attributePaths = ["originAccount", "targetAccount"])
  @Query(
    countQuery = "select count(tr.id) " +
      "from Transactions tr " +
      "where (tr.originAccount.id = :accountId or tr.targetAccount.id = :accountId)",
    value = "select tr " +
    "from Transactions tr " +
    "where (tr.originAccount.id = :accountId or tr.targetAccount.id = :accountId)" +
    "order by tr.audit.updatedAt desc" )
  fun findAllTransactions(@Param("accountId") accountId: Long, pageable: Pageable): Page<Transactions>
}
