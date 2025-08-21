package com.example.authspring.service;

import com.example.authspring.domain.Role;
import com.example.authspring.domain.User;
import com.example.authspring.domain.UserRepository;
import com.example.authspring.dto.request.LoginRequest;
import com.example.authspring.dto.request.SignupRequest;
import com.example.authspring.dto.response.TokenResponse;
import com.example.authspring.dto.response.UserResponse;
import com.example.authspring.exception.ApiException;
import com.example.authspring.exception.ErrorCode;
import com.example.authspring.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public UserResponse signup(SignupRequest request){
        if (userRepository.existsByUsername(request.username()))
            throw new ApiException(ErrorCode.USER_ALREADY_EXISTS);

        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .roles(Set.of(Role.USER))
                .build();
        userRepository.save(user);
        return new UserResponse(user.getUsername(), user.getNickname(), user.getRoles());
    }

    public TokenResponse login(LoginRequest request){
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new ApiException(ErrorCode.INVALID_CREDENTIALS));

                if (!passwordEncoder.matches(request.password(), user.getPassword()))
                    throw new ApiException(ErrorCode.INVALID_CREDENTIALS);

                var token = jwtTokenProvider.createToken(
                        user.getId(),
                        user.getUsername(),
                        user.getRoles().stream().map(Enum::name).collect(java.util.stream.Collectors.toSet()));
                return new TokenResponse(token);
    }

    public UserResponse grantAdmin(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        user.getRoles().add(Role.ADMIN);
        userRepository.save(user);
        return new UserResponse(user.getUsername(), user.getNickname(), user.getRoles());
    }
}
