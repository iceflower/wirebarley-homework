package com.wirebarley.homework.jpa.entities.transaction

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.math.BigDecimal

interface TransactionsRepository : JpaRepository<Transactions, String> {

  @Query(value = "select sum(tr.amount)" +
    "from Transactions tr join fetch tr.originAccount.id " +
    "where tr.transactionType = com.wirebarley.homework.vo.common.TransactionType.TRANSFER " +
    "and tr.originAccount.id = :accountId " +
    "and extract(DATE FROM tr.audit.updatedAt) = current_date" )
  fun sumTransferAmountsOfTheDay(@Param("accountId") accountId: Long): BigDecimal
}
