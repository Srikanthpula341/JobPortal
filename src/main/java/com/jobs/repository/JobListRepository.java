package com.jobs.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.jobs.entity.JobList;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface JobListRepository extends ReactiveMongoRepository<JobList, String> {

    @Query("{ 'companyName': { $regex: ?0, $options: 'i' } }")
    Flux<JobList> findByCompanyNameRegex(String companyName);
    
    
   // Flux<JobList> findAll(Pageable pageable);

    @Query("{'tag': {$regex: ?0, $options: 'i'}}")
    Flux<JobList> findByTagRegex(String tag, Pageable pageable);

    @Query("{'createdAt': {$lt: ?0}}")
    Mono<Void> deleteByCreatedAtBefore(LocalDate cutoffDate);

    Mono<Void> deleteAllByCreatedAtBefore(LocalDate date);

    Mono<Object> findAllByCreatedAtBefore(LocalDate cutoffDate);
}
