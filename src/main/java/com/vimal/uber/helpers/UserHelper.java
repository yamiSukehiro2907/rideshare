package com.vimal.uber.helpers;

import com.vimal.uber.dtos.UserDto;
import com.vimal.uber.models.User;

public class UserHelper {

    public static UserDto userToUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getRole().name(),
                user.getCreatedAt(),
                user.getUpdatedAt()

        );

    }
}
