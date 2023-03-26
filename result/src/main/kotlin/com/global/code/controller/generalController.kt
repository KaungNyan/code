package com.global.code.controller

import com.global.code.model.users
import com.global.code.service.generalService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.*
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.BadCredentialsException
import com.global.code.service.myUserDetailService
import org.springframework.security.authentication.AuthenticationManager;
import com.global.code.security.authenticationResponse
import com.global.code.security.jwtUtil

@RestController
@RequestMapping("/api")
class generalController {
    /*
    I named most of the classes very general names because I don't want to create a class just for one method

    I made "service" variable global because it should be accessible from all the methods in this class
    and "Autowired" anotation makes it possible just to declare and not to create an object

    lateinit allows non-null variable to be declared without initializing
    */
    @Autowired
    private lateinit var service: generalService;
	
	@Autowired
	private lateinit var authenticationManager: AuthenticationManager
	
	@Autowired
	private lateinit var jwtTokenUtil: jwtUtil
	
	@Autowired
	private lateinit var userDetailsService: myUserDetailService

    @GetMapping("/hello")
    fun hello(@RequestParam("name") name: String): Map<String, String> {
        /*
        It uses String Interpolation here
        The return type needs to be "Map" because in the requirement, it needs to return JSON response
        and also I think it needs to be a reference data type like List and Map, otherwise it won't work
        */
        val message = "Hello, $name!"
        return mapOf("message" to message)
    }

    @GetMapping("/rectangle/area")
    fun area(@RequestParam width: Double, @RequestParam height: Double): Map<String, Double> {
        /*
        call "area" method from apiService class with service object
        */
        val area = service.area(width, height)
        return mapOf("area" to area)
    }
	
	///////////////////////////
	
	@PostMapping("/authenticate")
	fun createAuthenticationToken(@RequestBody authenticationRequest: users): ResponseEntity<*> {
		try {
			authenticationManager.authenticate(
				UsernamePasswordAuthenticationToken(
					authenticationRequest.email,
					authenticationRequest.name
				)
			)
		} catch (e: BadCredentialsException) {
			throw Exception("Incorrect username or password", e)
		}
		
		val userDetails = userDetailsService.loadUserByUsername(authenticationRequest.name)
		val jwt = jwtTokenUtil.generateToken(userDetails)
		
		return ResponseEntity.ok(authenticationResponse(jwt))
	}
}