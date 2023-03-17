package com.jobs.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobSearchResults {

    private int count;
    private double mean;
    private List<JobDetails> results;

    // constructors, getters and setters
}
