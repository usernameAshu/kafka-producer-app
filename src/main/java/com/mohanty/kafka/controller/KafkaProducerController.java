package com.mohanty.kafka.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mohanty.kafka.service.KafkaProducerService;

@RestController
@RequestMapping("/kafka")
public class KafkaProducerController {

//	@Autowired
//	private KafkaTemplate<String, String> kafkaTemplate;

	@Autowired
	private KafkaProducerService service;

	private static final Logger LOGGER = LoggerFactory.getLogger("KafkaProducerController");

	@Value("${message.topic.name}")
	private String topicName;

	/**
	 * url: localhost:{server.port}/kafka/publish?message=
	 * 
	 * @param message
	 * @return
	 */
	@GetMapping("/publish")
	public String postMessage(@RequestParam(name = "message", required = true) String message) {

		String clientMsg = "publish successfully";

//		try {
//			LOGGER.info("Publishing message to topic...");
//			kafkaTemplate.send(topicName, message);
//		} catch (Exception e) {
//			clientMsg = "Exception in sending message to kafka topic";
//			LOGGER.error("Exception in sending message to kafka topic");
//			e.printStackTrace();
//		}

		return clientMsg;
	}

	/**
	 * url: localhost:{server.port}/kafka/startPublish
	 */
	@GetMapping("/startPublish")
	public void startPublishing() {
		try {
			service.getJobList();
			LOGGER.info("Get Job list done...");
			service.getJobDetails();
			LOGGER.info("Get job details done");
			LOGGER.info("Calling publishJobDetails()...");
			service.publishJobDetails();
			LOGGER.info("publishJobDetails() done");
		} catch (Exception e) {
			LOGGER.error("Error in startPublishing() method");
			e.printStackTrace();
		}
	}

}
