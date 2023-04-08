package com.jobs.handler;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.jobs.entity.JobList;
import com.jobs.repository.JobDetailsRepository;
import com.jobs.repository.JobListRepository;

import reactor.core.publisher.Mono;




@Component
public class JobDetailsHandler {

    @Autowired
    private JobDetailsRepository jobDetailsRepository;

    @Autowired
    private JobListRepository jobListRepository;
    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

  
    public Mono<ServerResponse> getJobList(ServerRequest request) {
        int page = request.queryParam("page").map(Integer::parseInt).orElse(0);
        int size = 12;
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

        Query query = new Query().with(sort).skip(page * size).limit(size);
        return reactiveMongoTemplate.find(query, JobList.class)
                .collectList()
                .flatMap(jobLists -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(jobLists))
                .onErrorResume(error -> {
                   
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Failed to fetch job list");
                });
    }
    public Mono<ServerResponse> getAllJobList(ServerRequest request) {

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");


        return jobListRepository.findAll()
                .collectList()
                .flatMap(jobLists -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(jobLists))
                .onErrorResume(error -> {

                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Failed to fetch job list");
                });
    }




    public Mono<ServerResponse> getJobByCompanyName(ServerRequest request) {
        String companyName = request.queryParam("companyName").orElse("");
                //.orElseThrow(() -> new IllegalArgumentException("Missing companyName query parameter"));

        return jobListRepository.findByCompanyNameRegex(companyName)
                .collectList()
                .flatMap(jobs -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(jobs))
                .onErrorResume(error -> {
                  
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .bodyValue("Failed to fetch jobs by company name");
                });
    }


    public Mono<ServerResponse> getJobDetailsById(ServerRequest request) {
        String jobId = request.pathVariable("id");

        return jobDetailsRepository.findById(jobId)
                .flatMap(job -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(job))
                .switchIfEmpty(ServerResponse.notFound().build())
                .onErrorResume(error -> {
                    
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .bodyValue("Failed to fetch job details by ID");
                });
    }
    
    public Mono<ServerResponse> getJobListByTag(ServerRequest request) {
        String tag = request.queryParam("tag").orElse("");

        int page = Integer.parseInt(request.queryParam("page").orElse("0"));
        int size = Integer.parseInt(request.queryParam("size").orElse("12"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        if (tag.isEmpty()) {
            return jobListRepository.findByTagRegex("", pageable)
                    .collectList()
                    .flatMap(jobs -> ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(jobs))
                    .onErrorResume(error -> {
                      
                        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .bodyValue("Failed to fetch all jobs");
                    });
        } else {
            return jobListRepository.findByTagRegex(tag, pageable)
                    .collectList()
                    .flatMap(jobs -> ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(jobs))
                    .onErrorResume(error -> {
                       
                        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .bodyValue("Failed to fetch jobs by tag");
                    });
        }
    }




}
