package com.kotlinspringboot.auth.models

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(value = ["hibernateLazyInitializer", "handler"])

@Entity
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id:Int = 0

    @Column(unique = true)
    var name = ""

    @Column(unique = true)
    var email = ""

    @Column
    var password = ""
        @JsonIgnore
        get() = field
        set(value) {
            val passwordEncoder = BCryptPasswordEncoder()
            field = passwordEncoder.encode(value).toString()
        }

    fun comparePassword(password:String):Boolean{

        return  BCryptPasswordEncoder().matches(password,this.password)

    }

}