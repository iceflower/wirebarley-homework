package com.wirebarley.homework.jpa.entities.account

import org.springframework.data.jpa.repository.JpaRepository

interface AccountsRepository : JpaRepository<Accounts, Long>
