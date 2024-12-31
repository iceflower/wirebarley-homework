package com.wirebarley.homework.jpa.entities.transaction

import org.springframework.data.jpa.repository.JpaRepository

interface TransactionsRepository : JpaRepository<Transactions, String>
