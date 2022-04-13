package com.example.userservice.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private AuthenticationManager authenticationManager;
	
	public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		log.info("Username is: {}", username);
		log.info("Password is: {}", password);
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
		return authenticationManager.authenticate(authenticationToken);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authentication) throws IOException, ServletException {
		 User user = (User)authentication.getPrincipal();
		 Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
		 List<String> authoritiesList = new ArrayList<>();
		 user.getAuthorities().forEach(authority -> {
			 authoritiesList.add(authority.getAuthority());
		 });
		 String access_token = JWT.create()
				 .withSubject(user.getUsername())
				 .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
				 .withIssuer(request.getRequestURL().toString())
				 .withClaim("roles", authoritiesList)
				 .sign(algorithm);
		 String refresh_token = JWT.create()
				 .withSubject(user.getUsername())
				 .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
				 .withIssuer(request.getRequestURL().toString())
				 .sign(algorithm);
//		 response.setHeader("access_token", access_token);
//		 response.setHeader("refresh_token", refresh_token);
		 Map<String, String> tokens = new HashMap<>();
		 tokens.put("access_token", access_token);
		 tokens.put("refresh_token", refresh_token);
		 response.setContentType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);
		 new ObjectMapper().writeValue(response.getOutputStream(), tokens);
	}

}
