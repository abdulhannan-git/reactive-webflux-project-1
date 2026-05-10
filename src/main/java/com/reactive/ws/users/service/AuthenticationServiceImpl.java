package com.reactive.ws.users.service;

import com.reactive.ws.users.data.UserEntity;
import com.reactive.ws.users.data.UserRepository;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final ReactiveAuthenticationManager reactiveAuthenticationManager;
    private final UserRepository userRepository;

    private AuthenticationServiceImpl(ReactiveAuthenticationManager reactiveAuthenticationManager, UserRepository userRepository) {
        this.reactiveAuthenticationManager = reactiveAuthenticationManager;
        this.userRepository = userRepository;
    }

    @Override
    public Mono<Map<String, String>> authenticate(String username, String password) {
        return reactiveAuthenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password))
                .then(getUserDetails(username))
                .map(userEntity -> createAuthResponse(userEntity));
    }

    private Mono<UserEntity> getUserDetails(String username){
        return userRepository.findByEmail(username);
    }

    private Map<String, String> createAuthResponse(UserEntity userEntity){
        Map<String, String> result = new HashMap<>();
        result.put("userId", userEntity.getId().toString());
        result.put("token", "JWT");
        return result;
    }
}
