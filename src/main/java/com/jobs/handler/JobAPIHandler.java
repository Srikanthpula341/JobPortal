package com.jobs.handler;

import com.jobs.entity.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.jobs.entity.JobList;
import com.jobs.entity.JobSearchResults;
import com.jobs.repository.JobDetailsRepository;
import com.jobs.repository.JobListRepository;

import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;

@Component
public class JobAPIHandler {

	@Autowired
	private JobDetailsRepository jobDetailsRepository;

	@Autowired
	private JobListRepository jobListRepository;

	@Autowired
	private WebClient webClient;

	private static final Logger log = LoggerFactory.getLogger(JobAPIHandler.class);

	private static String baseUrl = "https://api.adzuna.com/v1/api/jobs/in/search/50?";

	private static String keyUrl = "app_id=b9ef4285&app_key=a776aea724dfb21c2f24f9c6096f6d9a";

	private static String fetchValue = "&results_per_page=100";

	private static final String finalUrl = baseUrl + keyUrl + fetchValue;

	@Scheduled(fixedDelay = 1080000) // 86,400,000 ms / 80 = 1,080,000 ms
	public void scheduledFetchAndSaveJobs() {

		fetchAndSaveJobs(null).subscribe();
	}



	public Mono<ServerResponse> fetchAndSaveJobs(ServerRequest request) {
		Mono<JobSearchResults> jobApiList = webClient
				.get().uri(finalUrl)
				.retrieve()
				.bodyToMono(JobSearchResults.class);

		return jobApiList.flatMapIterable(JobSearchResults::getResults)
				.doOnNext(job -> job.setCategory(job.getCategory()))
				.flatMap(job -> {
					// Check if job with same ID exists in database
					return jobDetailsRepository.findById(job.getId()).flatMap(existingJob -> {
						// Job already exists in database, don't save it again
						return Mono.empty();
					}).switchIfEmpty(Mono.defer(() -> {

						return jobDetailsRepository.save(job)
								.flatMap(savedJob -> {
									JobList jobList = new JobList();
									jobList.setId(savedJob.getId());
									jobList.setTitle(savedJob.getTitle());
									jobList.setCompanyName(savedJob.getCompany().getDisplay_name()
											);
									String location = savedJob.getLocation().getArea().toString();
									location = location.replaceAll("[\\[\\]]", ""); // remove brackets
									String[] locationParts = location.split(",\\s*"); // split by comma and optional whitespace
									Collections.reverse(Arrays.asList(locationParts)); // reverse order
									String formattedLocation = String.join(", ", locationParts);
									jobList.setLocation(formattedLocation);
									jobList.setCreatedAt(savedJob.getCreated());
									jobList.setTag(savedJob.getCategory().getTag());
									//log.info("Saving JobList: {}", jobList);
									return jobListRepository.save(jobList);
								});
					}));
				}).collectList().flatMap(savedJobs -> {
					// If successful, return a success message with a 201 status
					return ServerResponse.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON)
							.body(Mono.just(new BaseResponse<>(savedJobs, "Successfully fetched and saved jobs")), BaseResponse.class);
				}).onErrorResume(error -> {
					if (error instanceof WebClientResponseException) {
						WebClientResponseException ex = (WebClientResponseException) error;
						log.error("WebClient error: {} - {}", ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
						return ServerResponse.status(ex.getStatusCode()).bodyValue("Failed to fetch jobs from external API");
					} else {

						log.error("Error while fetching and saving jobs", error);
						return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Failed to fetch and save jobs");
					}
				});
	}




}

