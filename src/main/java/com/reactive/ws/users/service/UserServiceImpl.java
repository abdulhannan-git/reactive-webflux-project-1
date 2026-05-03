package com.reactive.ws.users.service;

import com.reactive.ws.users.data.UserEntity;
import com.reactive.ws.users.data.UserRepository;
import com.reactive.ws.users.presentation.CreateUserRequest;
import com.reactive.ws.users.presentation.UserResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<UserResponse> createUser(Mono<CreateUserRequest> createUserRequestMono) {
        //Create UserEntity object
        return createUserRequestMono
                .mapNotNull(this::convertToEntity)
                .flatMap(userRepository::save)
                .mapNotNull(this::convertToUserResponse);
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

    public UserEntity convertToEntity(CreateUserRequest createUserRequest) {
        UserEntity entity = new UserEntity();

        //Won't work if classes have diff properties, if class have same properties works fine.
        //For nested classes also won't work.

        BeanUtils.copyProperties(createUserRequest, entity);
        return entity;
    }

}
