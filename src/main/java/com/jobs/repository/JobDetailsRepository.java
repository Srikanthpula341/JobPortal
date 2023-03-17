package com.jobs.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.jobs.entity.JobDetails;

public interface JobDetailsRepository extends ReactiveMongoRepository<JobDetails,String>{
}
