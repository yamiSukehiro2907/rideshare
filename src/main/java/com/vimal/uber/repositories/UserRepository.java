package com.vimal.uber.repositories;

import com.vimal.uber.models.User;

public interface UserRepository {

    void createUser(User user);

    User loadUserById(String userId);

    User loadUserByUsername(String username);

    void save(User user);

}
