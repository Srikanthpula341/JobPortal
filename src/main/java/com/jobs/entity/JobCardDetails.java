package com.jobs.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "jobCount")
public class JobCardDetails {

    private long todayJobsCount;
    private long yesterdayJobsCount;
    private long thisWeek;
    private long previousWeek;
    private long totalCount;

}
