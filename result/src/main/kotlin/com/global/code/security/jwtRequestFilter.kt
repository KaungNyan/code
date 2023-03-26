package com.global.code.security

import com.global.code.service.jwtUtil;
import com.global.code.service.myUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
class jwtRequestFilter(private val userDetailsService: myUserDetailService, private val util: jwtUtil) : OncePerRequestFilter() {
	
	@Throws(ServletException::class, IOException::class)
	override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
	    val authorizationHeader = request.getHeader("Authorization")
	    var username: String? = null
	    var jwt: String = ""
	
	    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
	        jwt = authorizationHeader.substring(7)
	        username = util.extractUsername(jwt)
	    }
	
	    if (username != null && SecurityContextHolder.getContext().authentication == null) {
	        val userDetails = userDetailsService.loadUserByUsername(username)
	
	        if (util.validateToken(jwt, userDetails)) {
	            val usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
	            usernamePasswordAuthenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
	            SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken
	        }
	    }
		
	    chain.doFilter(request, response)
	}
}