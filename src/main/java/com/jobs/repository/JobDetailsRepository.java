package com.jobs.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.jobs.entity.JobDetails;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface JobDetailsRepository extends ReactiveMongoRepository<JobDetails,String>{


    @Query("{'createdAt': {$lt: ?0}}")
    Mono<Void> deleteByCreatedAtBefore(LocalDate cutoffDate);
}
