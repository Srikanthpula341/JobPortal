package com.jobs.handler;

import com.jobs.entity.JobCardDetails;
import com.jobs.repository.JobCardDetailsRepository;
import com.jobs.repository.JobListRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.regex.Pattern;

@Component
public class JobsByLanguageHandler {

    @Autowired
    private JobListRepository jobListRepository;

    @Autowired
    private JobCardDetailsRepository jobCardDetailsRepository;

    private static final Logger log = LoggerFactory.getLogger(JobsByLanguageHandler.class);

//    public Mono<ServerResponse> searchJobsByTitleRegex(ServerRequest request) {
//        String keyword = request.queryParam("key").orElse("");
//        int page = Integer.parseInt(request.queryParam("page").orElse("0"));
//        int size = Integer.parseInt(request.queryParam("size").orElse("10"));
//
//        Pattern regex = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE);
//
//        Mono<Long> countMono = jobListRepository.countByTitleRegex(regex)
//                .filter(count -> count > 0);
//
//        return jobListRepository.findByTitleRegex(regex, page, size)
//                .collectList()
//                .flatMap(jobs -> ServerResponse.ok()
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .bodyValue(new JobListPage(jobs, countMono.block())))
//                .onErrorResume(error -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                        .bodyValue("Failed to search jobs by title regex"));
//    }



    private JobCountDates getJobCountDates(LocalDate now) {
        LocalDate lastDayStart = now.minusDays(1);
        LocalDate yesterdayStart = now.minusDays(2);
        LocalDate lastWeekStart = now.minusWeeks(1).with(DayOfWeek.MONDAY);
        LocalDate thisWeekStart = now.with(DayOfWeek.MONDAY);
        LocalDate thisWeekEnd = now.with(DayOfWeek.SUNDAY);
        LocalDate previousWeekStart = now.minusWeeks(2).with(DayOfWeek.MONDAY);
        LocalDate previousWeekEnd = now.minusWeeks(2).with(DayOfWeek.SUNDAY);

        return new JobCountDates(lastDayStart, yesterdayStart, lastWeekStart, thisWeekStart, thisWeekEnd, previousWeekStart, previousWeekEnd);
    }

    @Data
    @AllArgsConstructor
    private static class JobCountDates {
        private LocalDate lastDayStart;
        private LocalDate yesterdayStart;
        private LocalDate lastWeekStart;
        private LocalDate thisWeekStart;
        private LocalDate thisWeekEnd;
        private LocalDate previousWeekStart;
        private LocalDate previousWeekEnd;
    }
}
