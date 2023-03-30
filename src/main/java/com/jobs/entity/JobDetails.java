package com.jobs.entity;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "Job")
public class JobDetails {
	private String id;
	private String title;
	private String description;
	private String redirect_url;
	private String adref;
	private boolean salaryIsPredicted;
	private LocalDateTime created;
	private Location location;
	private Company company;
	private Category category;


}
