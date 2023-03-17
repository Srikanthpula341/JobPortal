//package com.jobs;
//
//
//import com.jobs.entity.JobDetails;
//import com.jobs.entity.JobSearchResults;
//import com.jobs.handler.JobAPIHandler;
//import com.jobs.repository.JobDetailsRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//
//import java.util.Arrays;
//import java.util.Collections;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//public class JobApiHandlerTest {
//
//    @InjectMocks
//    private JobAPIHandler jobDetailsHandler;
//
//    @Mock
//    private JobDetailsRepository jobDetailsRepository;
//
//    @Mock
//    private WebClient webClient;
//
//    @Mock
//    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
//
//    @Mock
//    private WebClient.RequestHeadersSpec requestHeadersSpec;
//
//    @Mock
//    private WebClient.ResponseSpec responseSpec;
//
////    @Test
////    void fetchAndSaveJobs() {
////        JobDetails job1 = new JobDetails();
////        job1.setId("1");
////        job1.setTitle("Job 1");
////
////        JobDetails job2 = new JobDetails();
////        job2.setId("2");
////        job2.setTitle("Job 2");
////
////        JobSearchResults jobSearchResults = new JobSearchResults();
////        jobSearchResults.setResults(Arrays.asList(job1, job2));
////
////        when(webClient.get()).thenReturn(requestHeadersUriSpec);
////        when(requestHeadersUriSpec.uri(any(String.class))).thenReturn(requestHeadersSpec);
////        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
////        when(responseSpec.bodyToMono(JobSearchResults.class)).thenReturn(Mono.just(jobSearchResults));
////
////        when(jobDetailsRepository.findById(job1.getId())).thenReturn(Mono.empty());
////        when(jobDetailsRepository.save(job1)).thenReturn(Mono.just(job1));
////
////        when(jobDetailsRepository.findById(job2.getId())).thenReturn(Mono.just(job2));
////
////        StepVerifier.create(jobDetailsHandler.fetchAndSaveJobs(null))
////                .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
////                .verifyComplete();
////    }
//}
//
