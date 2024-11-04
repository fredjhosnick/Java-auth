package com.kotlinspringboot.auth.services

import com.kotlinspringboot.auth.models.User
import com.kotlinspringboot.auth.repositories.UserRepository
import org.springframework.stereotype.Service

@Service

class UserService(private  var userRepository: UserRepository) {

    fun save(user: User): User {
        return this.userRepository.save(user)
    }
    fun findByEmail(email: String): User? {
        return this.userRepository.findByEmail(email)
    }
    fun getById(id: Int): User? {
        return this.userRepository.findById(id).orElse(null)
    }
}