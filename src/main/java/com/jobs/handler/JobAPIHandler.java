package com.jobs.handler;

import com.jobs.entity.BaseResponse;
import com.jobs.entity.JobList;
import com.jobs.entity.JobSearchResults;
import com.jobs.repository.JobDetailsRepository;
import com.jobs.repository.JobListRepository;
import org.slf4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.clearbit.ApiException;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import com.clearbit.ApiException;


@Component
public class JobAPIHandler {

	@Autowired
	private JobDetailsRepository jobDetailsRepository;

	@Autowired
	private JobListRepository jobListRepository;

	@Autowired
	private WebClient webClient;

	private static final Logger log = LoggerFactory.getLogger(JobAPIHandler.class);

	private static String baseUrl = "https://api.adzuna.com/v1/api/jobs/in/search/500?";

	private static String keyUrl = "app_id=b9ef4285&app_key=a776aea724dfb21c2f24f9c6096f6d9a";

	private static String fetchValue = "&results_per_page=2";

	private static final String GOOGLE_SEARCH_URL = "https://www.google.com/search?q=";
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36";

	private static final String finalUrl = baseUrl + keyUrl + fetchValue;

	@Scheduled(fixedDelay = 1080000) // 86,400,000 ms / 80 = 1,080,000 ms
	public void scheduledFetchAndSaveJobs() {
		fetchAndSaveJobs(null).subscribe();
	}

//	CompanyHandler companyHandler = new CompanyHandler();



	public Mono<ServerResponse> testApi (ServerRequest request){
		return ServerResponse.status(HttpStatus.OK).bodyValue("Test Api");
	}

	public Mono<ServerResponse> fetchAndSaveJobs(ServerRequest request) {
		Mono<JobSearchResults> jobApiList = webClient
				.get().uri(finalUrl)
				.retrieve()
				.bodyToMono(JobSearchResults.class);

		return jobApiList.flatMapIterable(JobSearchResults::getResults).log()
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
									jobList.setLocation(savedJob.getLocation().getArea().toString());
									jobList.setCreatedAt(savedJob.getCreated().toString());
									jobList.setTag(savedJob.getCategory().getTag());
									String companyName = job.getCompany().getDisplay_name();
									String imgUrl="";
									//companyHandler.getCompanyLogoUrl(companyName);


									if (imgUrl != null || imgUrl!="") {
										jobList.setCompanyLogo(imgUrl);
									} else {
										jobList.setCompanyLogo("");
									}
									//for(int i=0;i<10;i++){
										System.out.println(companyName);
										System.out.println(imgUrl);
									//}
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

	public String searchCompanyLogo(String companyName) {
		try {
			String searchQuery = URLEncoder.encode(companyName + " logo", StandardCharsets.UTF_8);

			Document doc = Jsoup.connect("https://www.google.com/search?q=" + searchQuery)
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")
					.get();

			Elements imgElements = doc.select("img[data-src]");

			if (imgElements.size() > 0) {
				Element firstImgElement = imgElements.first();
				String imgUrl = firstImgElement.attr("data-src");
				return imgUrl;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}












