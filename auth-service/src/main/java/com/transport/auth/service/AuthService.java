package com.transport.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.transport.auth.dto.AuthResponse;
import com.transport.auth.dto.LoginRequest;
import com.transport.auth.dto.RegisterRequest;
import com.transport.auth.entity.User;
import com.transport.auth.repository.UserRepository;
import com.transport.auth.security.JwtService;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {

		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	public void registrar(RegisterRequest request) {

		if (userRepository.existsByUsername(request.username())) {
			throw new RuntimeException("El usuario ya existe");
		}

		User user = User.builder().username(request.username()).password(passwordEncoder.encode(request.password()))
				.build();

		userRepository.save(user);
	}

	public AuthResponse login(LoginRequest request) {

		User user = userRepository.findByUsername(request.username())
				.orElseThrow(() -> new RuntimeException("Usuario o contraseña incorrectos"));

		if (!passwordEncoder.matches(request.password(), user.getPassword())) {

			throw new RuntimeException("Usuario o contraseña incorrectos");
		}

		String token = jwtService.generarToken(user.getUsername());

		return new AuthResponse(token);
	}
}