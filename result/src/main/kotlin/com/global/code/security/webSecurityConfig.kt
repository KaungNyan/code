package com.global.code.security

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
@EnableWebSecurity
class webSecurityConfig : WebSecurityConfigurerAdapter() {
	@Autowired
	private lateinit var myUserDetailsService: UserDetailsService
	
	@Autowired
	private lateinit var requestFilter: jwtRequestFilter
	
	@Autowired
	@Throws(Exception::class)
	fun configureGlobal(auth: AuthenticationManagerBuilder) {
	    auth.userDetailsService(myUserDetailsService)
	}
	
	@Bean
	fun passwordEncoder(): PasswordEncoder {
	    return NoOpPasswordEncoder.getInstance()
	}
	
	@Throws(Exception::class)
	override fun configure(httpSecurity: HttpSecurity) {
	    httpSecurity.csrf().disable()
	        .authorizeRequests().antMatchers("/authenticate").permitAll()
	        .anyRequest().authenticated().and()
	        .exceptionHandling().and().sessionManagement()
	        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	    httpSecurity.addFilterBefore(requestFilter, UsernamePasswordAuthenticationFilter::class.java)
	}
	
	@Throws(Exception::class)
	@Bean
	override fun authenticationManagerBean(): AuthenticationManager {
	    return super.authenticationManagerBean()
	}
}