package com.vimal.uber.repositories;

import com.vimal.uber.entities.User;
import org.bson.types.ObjectId;

public interface UserRepository {

    void createUser(User user);

    User loadUserById(String userId);

    User loadUserByUsername(String username);

    void save(User user);

}
