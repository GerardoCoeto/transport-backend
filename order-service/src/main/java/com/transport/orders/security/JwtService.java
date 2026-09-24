package com.transport.orders.security;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private static final String SECRET = "mi-clave-super-secreta-para-jwt-transportes-2026";

	private SecretKey getKey() {
		return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
	}

	public String obtenerUsername(String token) {

		Claims claims = Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();

		return claims.getSubject();
	}

	public boolean validarToken(String token) {

		try {

			Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token);

			return true;

		} catch (Exception e) {

			return false;
		}
	}
}