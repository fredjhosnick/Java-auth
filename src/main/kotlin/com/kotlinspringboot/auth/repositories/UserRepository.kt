package com.kotlinspringboot.auth.repositories

import com.kotlinspringboot.auth.models.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User,Int> {
    fun findByEmail(email: String): User?
}