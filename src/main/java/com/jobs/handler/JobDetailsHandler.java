package com.jobs.handler;


import com.jobs.entity.CompanyDto;
import com.jobs.entity.JobTitleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.function.BodyInserters;
import com.jobs.entity.JobList;
import com.clearbit.ApiException;
import com.clearbit.client.api.CombinedApi;
import com.jobs.repository.JobDetailsRepository;
import com.jobs.repository.JobListRepository;

import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


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



    public Flux<CompanyDto> getCompanyList() {
        Map<String, Integer> companyMap = new HashMap<>();
        AtomicInteger id = new AtomicInteger(1);
        return jobListRepository.findAll()
                .flatMap(jobList -> {
                    String companyName = jobList.getCompanyName();
                    if (!companyMap.containsKey(companyName)) {
                        int companyId = id.getAndIncrement();
                        companyMap.put(companyName, companyId);
                        return Mono.just(CompanyDto.builder()
                                .companyName(companyName)
                                .id(companyId)
                                .build());
                    } else {
                        return Mono.empty();
                    }
                })
                .distinct()
                .sort(Comparator.comparing(CompanyDto::getCompanyName))
                .onErrorResume(ex -> {
                    String errorMsg = "Failed to retrieve company list: " + ex.getMessage();
                    return Flux.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorMsg, ex));
                });
    }
    public Flux<JobTitleDto> getJobTitleList() {
        Map<String, Integer> jobTitleMap = new HashMap<>();
        AtomicInteger id = new AtomicInteger(1);
        return jobListRepository.findAll()
                .flatMap(jobList -> {
                    String jobTitle = jobList.getTitle();
                    if (!jobTitleMap.containsKey(jobTitle)) {
                        int titelId = id.getAndIncrement();
                        jobTitleMap.put(jobTitle, titelId);
                        return Mono.just(JobTitleDto.builder()
                                .title(jobTitle)
                                .id(titelId)
                                .build());
                    } else {
                        return Mono.empty();
                    }
                })
                .distinct()
               // .sort(Comparator.comparing(CompanyDto::getCompanyName))
                .sort(Comparator.comparing(JobTitleDto::getTitle))
                .onErrorResume(ex -> {
                    String errorMsg = "Failed to retrieve company list: " + ex.getMessage();
                    return Flux.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorMsg, ex));
                });
    }


    public Mono<ServerResponse> getCompanyList(ServerRequest serverRequest) {
        Flux<CompanyDto> companyList = getCompanyList();
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(companyList, CompanyDto.class)
                .onErrorResume(ex -> {
                    String errorMsg = "Failed to retrieve company list: " + ex.getMessage();
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(BodyInserters.fromValue(errorMsg));
                });
    }

    public Mono<ServerResponse> getJobTitleList(ServerRequest serverRequest) {
        Flux<JobTitleDto> jobTitleList = getJobTitleList();
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(jobTitleList, CompanyDto.class)
                .onErrorResume(ex -> {
                    String errorMsg = "Failed to retrieve company list: " + ex.getMessage();
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(BodyInserters.fromValue(errorMsg));
                });
    }
    public Mono<ServerResponse> deleteOldRecords(ServerRequest request) {
        int limit = 2000; // number of old records to keep
        LocalDateTime threshold = LocalDateTime.now().minusWeeks(1); // threshold for old records

        return jobListRepository.findAll() // fetch all job lists
                .sort(Comparator.comparing(JobList::getCreatedAt)) // sort by created date
                .collectList()
                .flatMap(jobLists -> {
                    List<String> jobListIdsToDelete = jobLists.stream()
                            .filter(jobList -> LocalDateTime.parse(jobList.getCreatedAt()).isBefore(threshold)) // only keep job lists created within the threshold
                            .skip(limit) // skip the first 20 job lists
                            .map(JobList::getId)
                            .collect(Collectors.toList());

                    // delete job lists
                    Mono<Void> deleteJobListMono = jobListRepository.deleteAllById(jobListIdsToDelete)
                            .doOnSuccess(count -> {
                                System.out.println("Deleted " + count + " job lists.");
                                jobListIdsToDelete.forEach(id -> System.out.println("Deleted job list with ID: " + id));
                            });

                    Mono<Void> deleteJobDetailsMono = jobDetailsRepository.deleteAllById(jobListIdsToDelete)
                            .doOnSuccess(count -> {
                                System.out.println("Deleted " + count + " job details.");
                                jobListIdsToDelete.forEach(id -> System.out.println("Deleted job details with job list ID: " + id));
                            });


                    // combine the two delete operations
                    return Mono.zip(deleteJobListMono, deleteJobDetailsMono);
                })
                .then(ServerResponse.noContent().build());
    }


















}
