package com.global.code.controller

import com.global.code.model.users
import com.global.code.repository.userRepository
import com.global.code.service.userService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.http.ResponseEntity

import java.util.*

import jakarta.servlet.http.HttpServletResponse
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import jakarta.servlet.http.Cookie

@RestController
@RequestMapping("/user")
class userController(private val service: userService) {
	/*
    I don't use @Autowired here because if it can be done like this in Service with Repository,
 	it might also be able to do in Controller with Service
	and I look it up to sure it's ok or not to use and it said it's called injection and can be used like this too
    */

    @GetMapping("/getById/{id}")
	@PreAuthorize("hasRole('ROLE_USER')")
    fun getUserById(@PathVariable id: Long): users {
		return service.getUserById(id);
    }

    @PostMapping("/save")
	@PreAuthorize("hasRole('ROLE_USER')")
    fun createUser(@RequestBody user: users): users {
        return service.createUser(user);
    }

    @PutMapping("/update/{id}")
	@PreAuthorize("hasRole('ROLE_USER')")
    fun updateUserById(@PathVariable id: Long, @RequestBody user: users): users {
        return service.updateUserById(id, user);
    }

    @DeleteMapping("/delete/{id}")
	@PreAuthorize("hasRole('ROLE_USER')")
    fun deleteUserById(@PathVariable id: Long) {
        service.deleteUserById(id);
    }
	
	/////////////////////////////////////////////
	
	@PostMapping("register")
    fun register(@RequestBody body: users): ResponseEntity<users> {
        val user = users()
        user.name = body.name
        user.email = body.email

        return ResponseEntity.ok(this.service.createUser(user))
    }

    @PostMapping("login")
    fun login(@RequestBody body: users, response: HttpServletResponse): ResponseEntity<Any> {
        val user = this.service.getUserByEmail(body.email)
            ?: return ResponseEntity.badRequest().body("user not found!")

        if (!user.name.equals(body.name)) {
            return ResponseEntity.badRequest().body("invalid password!")
        }

        val issuer = user.id.toString()

        val jwt = Jwts.builder()
            .setIssuer(issuer)
            .setExpiration(Date(System.currentTimeMillis() + 60 * 24 * 1000)) // 1 day
            .signWith(SignatureAlgorithm.HS512, "secret").compact()

        val cookie = Cookie("jwt", jwt)
        cookie.isHttpOnly = true

        response.addCookie(cookie)

        return ResponseEntity.ok("success")
    }

    @GetMapping("user")
    fun user(@CookieValue("jwt") jwt: String?): ResponseEntity<Any> {
        try {
            if (jwt == null) {
                return ResponseEntity.status(401).body("unauthenticated")
            }

            val body = Jwts.parser().setSigningKey("secret").parseClaimsJws(jwt).body

            return ResponseEntity.ok(this.service.getUserById(body.issuer.toLong()))
        } catch (e: Exception) {
            return ResponseEntity.status(401).body("unauthenticated")
        }
    }

    @PostMapping("logout")
    fun logout(response: HttpServletResponse): ResponseEntity<Any> {
        val cookie = Cookie("jwt", "")
        cookie.maxAge = 0

        response.addCookie(cookie)

        return ResponseEntity.ok("success")
    }
}