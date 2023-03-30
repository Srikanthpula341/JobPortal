package com.jobs.router;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import com.jobs.handler.JobDetailsHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.jobs.handler.JobAPIHandler;

@Configuration
//@CrossOrigin(origins = "http://localhost:4200")
public class JobRouterConfig {
	
	@Autowired
	private JobAPIHandler jobHandler;

	@Autowired
	private JobDetailsHandler jobDetailsHandler;
	
	@Bean
	public RouterFunction<ServerResponse> routerFunctionOrder() {

		return RouterFunctions.route()
				.path("/api/job.com/v1",
						b1 -> b1.nest(accept(APPLICATION_JSON),
								b2 -> b2

										.GET("/jobs",jobHandler::fetchAndSaveJobs)
										.GET("/job-list",jobDetailsHandler::getJobList)
										.GET("/job-company",jobDetailsHandler::getJobByCompanyName)
										.GET("/job/{id}",jobDetailsHandler::getJobDetailsById)
										.GET("/job-tag",jobDetailsHandler::getJobListByTag)
										.GET("/company",jobDetailsHandler::getCompanyList)
										.GET("/title",jobDetailsHandler::getJobTitleList)
										.DELETE("/delete",jobDetailsHandler::deleteOldRecords)
										.GET(jobHandler::testApi)

								
								))
				.build();

	}

	

}
