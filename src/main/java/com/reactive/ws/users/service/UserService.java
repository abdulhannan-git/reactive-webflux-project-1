package com.reactive.ws.users.service;

import com.reactive.ws.users.presentation.model.CreateUserRequest;
import com.reactive.ws.users.presentation.model.UserResponse;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;


public interface UserService extends ReactiveUserDetailsService {
    Mono<UserResponse> createUser(Mono<CreateUserRequest> createUserRequestMono);
    Mono<UserResponse> getUserById(UUID id);
    Flux<UserResponse> findAll(int page, int limit);
}
