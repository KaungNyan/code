package com.global.code.repository

import com.global.code.model.users
import org.springframework.data.jpa.repository.JpaRepository

interface userRepository : JpaRepository<users, Long> {
	fun findByEmail(email:String): users?
}