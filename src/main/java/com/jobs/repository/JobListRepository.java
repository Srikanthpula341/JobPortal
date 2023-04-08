package com.jobs.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.jobs.entity.JobList;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public interface JobListRepository extends ReactiveMongoRepository<JobList, String> {



    @Query("{ 'companyName': { $regex: ?0, $options: 'i' } }")
    Flux<JobList> findByCompanyNameRegex(String companyName);

    @Query("{'tag': {$regex: ?0, $options: 'i'}}")
    Flux<JobList> findByTagRegex(String tag, Pageable pageable);

    Flux<JobList> findByCreatedAtBetween(ZonedDateTime from, ZonedDateTime to);
}
