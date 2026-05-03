package com.reactive.ws.users.service;

import com.reactive.ws.users.presentation.CreateUserRequest;
import com.reactive.ws.users.presentation.UserResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;


public interface UserService {
    Mono<UserResponse> createUser(Mono<CreateUserRequest> createUserRequestMono);
    Mono<UserResponse> getUserById(UUID id);
    Flux<UserResponse> findAll(int page, int limit);
}
