package com.jobs.handler;

import com.jobs.entity.JobCardDetails;
import com.jobs.repository.JobCardDetailsRepository;
import com.jobs.repository.JobListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Component
public class JobCardDetailsHandler {


    @Autowired
    private JobCardDetailsRepository jobCardDetailsRepository;

    @Autowired
    private JobListRepository jobListRepository;

    public Mono<ServerResponse> getCountByCreatedAtBetween(ServerRequest request) {
        ZonedDateTime from = ZonedDateTime.now().minusYears(2);
        ZonedDateTime to = ZonedDateTime.now();

        Mono<Long> countMono = jobListRepository.findByCreatedAtBetween(from, to)
                .count();

        return ServerResponse.ok().body(countMono, Long.class);
    }












}

