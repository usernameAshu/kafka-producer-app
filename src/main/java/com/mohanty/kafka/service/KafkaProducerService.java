package com.mohanty.kafka.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.ibm.dgwservice.modelsJobDetails.DgwJobDetailsResponse;
import com.ibm.dgwservice.modelsJobDetails.JobDetails;
import com.ibm.dgwservice.modelsJobList.ClientResponseModel;

@Service
public class KafkaProducerService {
	
	@Autowired
	private KafkaTemplate<String, JobDetails> kafkaTemplate;

	private static final Logger LOGGER = LoggerFactory.getLogger("KafkaProducerService");

	@Value("${message.topic.name}")
	private String topicName;
	
	@Autowired
	private RestTemplate restTemplate;
	
	private List<String> correlationIdList = new ArrayList<>();

	private DgwJobDetailsResponse dgwJobDetailsResponse;

	private Map<String,DgwJobDetailsResponse> jobDetailsMap = new HashMap<>();;
	
	public void getJobList() {
		String jobListUrl = "http://localhost:8080/dgw/joblist";
	
		try {
		ResponseEntity<ClientResponseModel> responseEntity = restTemplate.exchange(jobListUrl, HttpMethod.GET, null, 
				ClientResponseModel.class);
		correlationIdList = responseEntity.getBody().getCorrelationIdList();
		} catch (RestClientException e) {
			LOGGER.error("Error while fetching job list");
			e.printStackTrace();
		} catch (Exception e) {
			LOGGER.error("Error while fetching job list for");
			e.printStackTrace();
		}
	}
	
	public void getJobDetails() {
		correlationIdList.stream()
		.forEach(correlationId -> {
			String jobDetailsUrl = "http://localhost:8080/dgw/getJobDetails?correlationId="+correlationId;
			try {
			ResponseEntity<DgwJobDetailsResponse> responseEntity = restTemplate.exchange(jobDetailsUrl, HttpMethod.GET, null, 
					DgwJobDetailsResponse.class);
			dgwJobDetailsResponse = responseEntity.getBody();
			jobDetailsMap.put(correlationId, dgwJobDetailsResponse);
			} catch (RestClientException e) {
				LOGGER.error("Error while fetching job details");
				e.printStackTrace();
			} catch (Exception e) {
				LOGGER.error("Error while fetching job details for");
				e.printStackTrace();
			}
		});
	}
	
	public void publishJobDetails() {
		jobDetailsMap.forEach((k,v) -> {
			try {
				LOGGER.info("Publishing message to topic...");
				if (v.getJobDetails() != null) {
					kafkaTemplate.send(topicName, v.getJobDetails());
				}
			} catch (Exception e) {
				LOGGER.error("Exception in sending message to kafka topic");
				e.printStackTrace();
			}
		});
	}
}
