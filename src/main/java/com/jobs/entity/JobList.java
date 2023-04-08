package com.jobs.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "joblist")
public class JobList {
	
	private String id;
	private String title;
	private String companyName;
	private String tag;
	private String location;
	private LocalDateTime createdAt;
	

}
