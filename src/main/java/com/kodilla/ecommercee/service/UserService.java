package com.kodilla.ecommercee.service;

import com.kodilla.ecommercee.dto.request.CreateUserRequest;
import com.kodilla.ecommercee.dto.request.LockUserRequest;
import com.kodilla.ecommercee.dto.request.UpdateUserRequest;
import com.kodilla.ecommercee.dto.request.UserCredentialsRequest;
import com.kodilla.ecommercee.dto.response.UserLockedResponse;
import com.kodilla.ecommercee.dto.response.UserResponse;
import com.kodilla.ecommercee.entity.User;
import com.kodilla.ecommercee.exception.*;
import com.kodilla.ecommercee.mapper.UserMapper;
import com.kodilla.ecommercee.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();

        return userMapper.mapToUsersList(users);
    }

    public UserResponse getUser(Long userId) throws UserNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        return userMapper.toUserResponse(user);
    }

    public UserResponse createUser(CreateUserRequest createUserRequest) throws NullValueException, UsernameAlreadyExistsException {
        if (userRepository.findUserByUsername(createUserRequest.username()).isPresent()) {
            throw new UsernameAlreadyExistsException(createUserRequest.username());
        }

        if (isNullOrEmpty(createUserRequest.username()) || isNullOrEmpty(createUserRequest.password()) || isNullOrEmpty(createUserRequest.address())) {
            throw new NullValueException();
        }
        User newUser = new User(createUserRequest.username(), createUserRequest.password(), createUserRequest.address());
        newUser.setLocked(false);

        userRepository.save(newUser);

        return userMapper.toUserResponse(newUser);
    }

    public UserResponse updateUser(UpdateUserRequest updateUserRequest) throws UserNotFoundException, UsernameAlreadyExistsException, NullValueException {
        User user = userRepository.findById(updateUserRequest.id()).orElseThrow(() -> new UserNotFoundException(updateUserRequest.id()));
        boolean isUpdated = false;


        if (isNullOrEmpty(updateUserRequest.username()) || isNullOrEmpty(updateUserRequest.password()) || isNullOrEmpty(updateUserRequest.address())) {
            throw new NullValueException();
        }

        if (!updateUserRequest.username().equals(user.getUsername())) {
            if (userRepository.findUserByUsername(updateUserRequest.username()).isPresent()) {
                throw new UsernameAlreadyExistsException(updateUserRequest.username());
            }
            user.setUsername(updateUserRequest.username());
            isUpdated = true;
        }

        if (!updateUserRequest.password().equals(user.getPassword())) {
            user.setPassword(updateUserRequest.password());
            isUpdated = true;
        }

        if (!updateUserRequest.address().equals(user.getAddress())) {
            user.setAddress(updateUserRequest.address());
            isUpdated = true;
        }

        if (isUpdated) {
            userRepository.save(user);
        }

        return userMapper.toUserResponse(user);
    }

    private boolean isNullOrEmpty(String requestData) {
        return requestData == null || requestData.isEmpty();
    }

    public void deleteUser(Long userId) throws UserNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        userRepository.delete(user);
    }

    public UserLockedResponse lockUser(LockUserRequest lockUserRequest) throws UserNotFoundException, NullValueException {
        User user = userRepository.findById(lockUserRequest.userId()).orElseThrow(() -> new UserNotFoundException(lockUserRequest.userId()));

        if (isNullOrEmpty(lockUserRequest.username()) || isNullOrEmpty(lockUserRequest.password())) {
            throw new NullValueException();
        }

        if (user.getUsername().equals(lockUserRequest.username()) && user.getPassword().equals(lockUserRequest.password())) {
            user.setLocked(true);
        }

        userRepository.save(user);

        return userMapper.mapToUserLockedResponse(user);
    }

    public Integer loginUser(UserCredentialsRequest userCredentialsRequest) throws InvalidCredentialsException, UsernameNotFoundException, NullValueException {

        User user = userRepository.findUserByUsername(userCredentialsRequest.username()).orElseThrow(() -> new UsernameNotFoundException(userCredentialsRequest.username()));
        SecureRandom random = new SecureRandom();
        int min = 1000000;
        int max = 2000000;
        int token = random.nextInt(max - min) + min;

        if (isNullOrEmpty(userCredentialsRequest.username()) || isNullOrEmpty(userCredentialsRequest.password())) {
            throw new NullValueException();
        }
        if (user.getPassword().equals(userCredentialsRequest.password())) {
            user.setToken(token);
            user.setTokenCreationTime(LocalDateTime.now());
            userRepository.save(user);
        } else {
            throw new InvalidCredentialsException();
        }
        return token;
    }

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void cleanUpExpiredTokens() {
        LocalDateTime currentTime = LocalDateTime.now();
        List<User> users = userRepository.findAll();

        users.stream()
                .filter(user -> user.getTokenCreationTime() != null && user.getTokenCreationTime().plusHours(1).isBefore(currentTime))
                .forEach(user -> {
                    user.setToken(null);
                    user.setTokenCreationTime(null);
                    userRepository.save(user);
                });
    }
}
