package com.vimal.uber.repositories;

import com.vimal.uber.entities.User;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public void createUser(User user) {
        mongoTemplate.insert(user, "users");
    }

    @Override
    public User loadUserById(String userId) {
        return mongoTemplate.findById(userId, User.class);
    }

    public User loadUserByUsername(String username) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));
        return mongoTemplate.findOne(query, User.class);
    }


    @Override
    public void save(User user) {
        mongoTemplate.save(user, "users");
    }

    @Override
    public User findById(ObjectId userId) {
        return null;
    }

}