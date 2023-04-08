package com.jobs.repository;

import com.jobs.entity.JobCardDetails;
import com.jobs.entity.JobList;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface JobCardDetailsRepository extends ReactiveMongoRepository<JobCardDetails, ObjectId> {
}



