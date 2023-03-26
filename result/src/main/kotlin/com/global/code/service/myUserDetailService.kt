package com.global.code.service

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
class myUserDetailService : UserDetailsService {
	override fun loadUserByUsername(username: String): UserDetails {
	    return User("foo", "foo", ArrayList())
	}
}