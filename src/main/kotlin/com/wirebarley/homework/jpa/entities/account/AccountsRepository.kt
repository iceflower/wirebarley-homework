package com.wirebarley.homework.jpa.entities.account

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface AccountsRepository : JpaRepository<Accounts, Long> {

  @Query(value = "select case when (count(a) = 1) then true else false end " +
    "from Accounts a " +
    "where a.isActive = true and a.id = :id")
  fun existById(@Param("id") accountId: Long): Boolean

  @Query(value = "select case when (count(a) = 1) then true else false end " +
    "from Accounts a " +
    "where a.isActive = true and a.ownerPhoneNumber = :phoneNumber")
  fun existsByOwnerPhoneNumber(@Param("phoneNumber") ownerPhoneNumber: String): Boolean

  @Query(value = "select case when (count(a) = 1) then true else false end " +
    "from Accounts a " +
    "where a.isActive = true and a.ownerEmail = :email")
  fun existsByOwnerEmail(@Param("email") email: String): Boolean
}
