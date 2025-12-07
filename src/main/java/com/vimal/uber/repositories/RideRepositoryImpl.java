package com.vimal.uber.repositories;

import com.vimal.uber.enums.RideStatus;
import com.vimal.uber.models.Ride;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RideRepositoryImpl implements RideRepository {

    private final MongoTemplate mongoTemplate;


    @Override
    public Ride save(Ride ride) {
        return mongoTemplate.insert(ride, "rides");
    }

    @Override
    public List<Ride> findAll() {
        Query query = new Query();
        query.addCriteria(Criteria.where("rideStatus").is(RideStatus.REQUESTED));
        return mongoTemplate.find(query, Ride.class, "rides");
    }

    @Override
    public Ride findById(String id) {
        return mongoTemplate.findById(id, Ride.class, "rides");
    }

    @Override
    public Ride update(Ride ride) {
        return mongoTemplate.save(ride, "rides");
    }

    @Override
    public List<Ride> findByUserId(String userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        return mongoTemplate.find(query, Ride.class, "rides");
    }


}
