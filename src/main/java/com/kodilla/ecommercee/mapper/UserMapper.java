package com.kodilla.ecommercee.mapper;

import com.kodilla.ecommercee.dto.response.UserLockedResponse;
import com.kodilla.ecommercee.dto.response.UserResponse;
import com.kodilla.ecommercee.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserMapper {

    public UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getAddress(),
                user.getToken(),
                user.getTokenCreationTime(),
                user.isLocked()
        );
    }


    public List<UserResponse> mapToUsersList(List<User> users) {
        return users.stream()
                .map(this::toUserResponse)
                .toList();
    }

    public UserLockedResponse mapToUserLockedResponse(User user) {
        return new UserLockedResponse(
                user.getId(),
                user.getUsername(),
                user.isLocked()
        );
    }
}
