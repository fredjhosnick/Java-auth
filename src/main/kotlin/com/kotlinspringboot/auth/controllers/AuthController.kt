package com.kotlinspringboot.auth.controllers
import io.jsonwebtoken.security.Keys
import com.kotlinspringboot.auth.dtos.LoginDTO
import com.kotlinspringboot.auth.dtos.Message
import com.kotlinspringboot.auth.dtos.RegisterDTO
import com.kotlinspringboot.auth.models.User
import com.kotlinspringboot.auth.services.UserService
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.config.Elements.JWT
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.SignatureException
import java.util.*
import javax.crypto.SecretKey

@RestController
@RequestMapping("api")
class AuthController(private val userService: UserService) {
    private val key: SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)

    @PostMapping("register")
    fun register(@RequestBody body: RegisterDTO): ResponseEntity<User> {

        val user = User()
        user.name = body.name
        user.email = body.email
        user.password = body.password
        return ResponseEntity.ok(this.userService.save(user))
    }
    @PostMapping("login")
    fun login(@RequestBody body:LoginDTO, response:HttpServletResponse):ResponseEntity<Any>{
        val user = this.userService.findByEmail(body.email)
            ?: return ResponseEntity.badRequest().body(Message("user not found."))

        if(!user.comparePassword(body.password)){
            return ResponseEntity.badRequest().body(Message("invalid password."))
        }

        val issuer = user.id.toString()
        val jwt = Jwts.builder()
            .setIssuer(issuer)
            .setExpiration(Date(System.currentTimeMillis() + 60*60*24*1000 ))
            .signWith(key).compact()

        val cookie = Cookie( "jwt", jwt)
        cookie.isHttpOnly = true
        response.addCookie(cookie)

        return  ResponseEntity.ok(Message("Successfully logged in."))

    }

    @GetMapping("user")
    fun user(@CookieValue("jwt") jwt: String?): ResponseEntity<Any> {
        if(jwt == null){
            return ResponseEntity.status(401).body(Message("unauthenticated"))
        }
        try {

            val body = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).body
            return ResponseEntity.ok(this.userService.getById(body.issuer.toInt()))
        } catch (e: SignatureException) {
            return ResponseEntity.status(401).body(Message("invalid token"))
        }

    }
    @PostMapping("logout")
    fun logout(response: HttpServletResponse):ResponseEntity<Any> {
        val cookie = Cookie( "jwt", "")
        cookie.maxAge = 0

        response.addCookie(cookie)
        return ResponseEntity.ok(Message("succes"))
    }
}