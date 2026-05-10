package com.reactive.ws.users.service;

import com.reactive.ws.users.data.UserEntity;
import com.reactive.ws.users.data.UserRepository;
import com.reactive.ws.users.presentation.model.CreateUserRequest;
import com.reactive.ws.users.presentation.model.UserResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<UserResponse> createUser(Mono<CreateUserRequest> createUserRequestMono) {
        //Create UserEntity object
        return createUserRequestMono
                .flatMap(this::convertToEntity)
                .flatMap(userRepository::save)
                .mapNotNull(this::convertToUserResponse);
//                .onErrorMap(
//                        throwable -> {
//                            if (throwable instanceof DuplicateKeyException) {
//                                return new ResponseStatusException(HttpStatus.CONFLICT, throwable.getMessage());
//                            } else if (throwable instanceof DataIntegrityViolationException) {
//                                return new ResponseStatusException(HttpStatus.BAD_REQUEST, throwable.getMessage());
//                            } else {
//                                return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, throwable.getMessage());
//                            }
//                        }
//                );
    }

    @Override
    public Mono<UserResponse> getUserById(UUID id) {
        return userRepository.findById(id).map(userEntity -> convertToUserResponse(userEntity));
    }

    @Override
    public Flux<UserResponse> findAll(int page, int limit) {
        if (page > 0) {
            page = page - 1;
        }
        Pageable pageable = PageRequest.of(page, limit);
        return userRepository.findAllBy(pageable)
                .map(userEntity -> convertToUserResponse(userEntity));
    }

    private UserResponse convertToUserResponse(UserEntity userEntity) {
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(userEntity, userResponse);
        return userResponse;
    }

    public Mono<UserEntity> convertToEntity(CreateUserRequest createUserRequest) {
        return Mono.fromCallable(() -> {
            UserEntity entity = new UserEntity();
            //Won't work if classes have diff properties, if class have same properties works fine.
            //For nested classes also won't work.
            BeanUtils.copyProperties(createUserRequest, entity);
            entity.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
            return entity;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByEmail(username)
                .map(userEntity -> User
                        .withUsername(userEntity.getEmail())
                        .password(userEntity.getPassword())
                        .authorities(new ArrayList<>())
                        .build());
    }
}
