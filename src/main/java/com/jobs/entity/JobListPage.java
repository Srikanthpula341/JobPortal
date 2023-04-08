package com.jobs.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class JobListPage {
    private List<JobList> jobs;
    private long count;
}

