package com.buymore.backend.service;

import com.buymore.backend.dto.AdminCreateUserRequest;
import com.buymore.backend.dto.LoginRequest;
import com.buymore.backend.dto.LoginResponse;
import com.buymore.backend.dto.UserRegisterRequest;
import com.buymore.backend.dto.UserResponse;
import com.buymore.backend.entity.User;
import com.buymore.backend.exception.ConflictException;
import com.buymore.backend.exception.InvalidCredentialsException;
import com.buymore.backend.exception.ResourceNotFoundException;
import com.buymore.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse register(UserRegisterRequest request) {
        requireUniqueEmail(request.email());

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(User.Role.CUSTOMER)
                .build();

        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse createByAdmin(AdminCreateUserRequest request) {
        requireUniqueEmail(request.email());

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();

        return toResponse(userRepository.save(user));
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String token = UUID.randomUUID().toString();

        return new LoginResponse(token, toResponse(user));
    }

    public UserResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    private void requireUniqueEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Email already registered: " + email);
        }
    }

    private User findOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getCreatedAt());
    }
}
