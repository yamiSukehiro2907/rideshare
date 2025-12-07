package com.vimal.uber.repositories;

import com.vimal.uber.dtos.RideInfoRequest;
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
    public List<Ride> findAll(RideInfoRequest rideInfoRequest , String userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("pickUpLocation").is(rideInfoRequest.pickupLocation()));
        query.addCriteria(Criteria.where("dropLocation").is(rideInfoRequest.dropLocation()));
        query.addCriteria(Criteria.where("userId").is(userId));
        return mongoTemplate.find(query, Ride.class);
    }
}
