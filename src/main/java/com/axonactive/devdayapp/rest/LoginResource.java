package com.axonactive.devdayapp.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.axonactive.devdayapp.dto.LoginUser;
import com.axonactive.devdayapp.service.LoginService;

@RestController
@RequestMapping("/user")
public class LoginResource {
	
	private static final Logger log = LogManager.getLogger(LoginResource.class);

	private static final String ANONYMOUS_USER = "anonymousUser";
	
	@Autowired
	private LoginService loginService;

	@PostMapping("/register")
	public void registration(@RequestBody LoginUser loginUser) {
		log.info("POST request to register user. Username: ", loginUser.getUsername());
		
		try {
			loginService.registration(loginUser);
			
			loginService.login(loginUser.getUsername(), loginUser.getPassword());
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot register user. Exception: " + e.getMessage());
		}
	}

	@GetMapping("/login")
	public LoginUser login(@RequestParam String username, @RequestParam String password) {
		log.info("GET request to login. Username: ", username);
		try {
			LoginUser user = loginService.login(username, password);
			
			if (user != null) {
				log.info(String.format("User %s login successfully!", username));
				SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
			}
			return user;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User failed to login. Exception: " + e.getMessage());
		}
	}

	
	@GetMapping("/logout")
	public void logout() {
		if (SecurityContextHolder.getContext().getAuthentication() == null || ANONYMOUS_USER.equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User has not been logined yet.");
		}
		log.info("GET request to logout. Username: ", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		SecurityContextHolder.getContext().setAuthentication(null);
	}
}