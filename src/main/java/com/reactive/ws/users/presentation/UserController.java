package com.reactive.ws.users.presentation;

import com.reactive.ws.users.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    //@ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<UserResponse>> createUser(@RequestBody @Valid Mono<CreateUserRequest> createUserRequest) {
        return userService.createUser(createUserRequest).map(item -> ResponseEntity
                .status(HttpStatus.CREATED)
                .location(URI.create("/users" + item.getId()))
                .body(item));
    }

    @GetMapping("/{userId}")
    public Mono<ResponseEntity<UserResponse>> getUser(@PathVariable("userId") UUID userId) {
        return userService.getUserById(userId)
                .map(userResponse -> ResponseEntity.status(HttpStatus.OK).body(userResponse));
    }

    @GetMapping
    public ResponseEntity<Flux<UserResponse>> getUsers(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "50") int limit
    ) {
//        Flux<UserResponse> users = Flux.just(
//                new UserResponse(UUID.randomUUID(), "Abdul", "Hannan", "Hannan@test.com"),
//                new UserResponse(UUID.randomUUID(), "Abdul", "Mannan", "Mannan@test.com"),
//                new UserResponse(UUID.randomUUID(), "Abdul", "Haiyan", "Haiyan@test.com")
//        );
//        return ResponseEntity.ok(users);
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAll(offset, limit));

    }
}
