package com.listener.waterFlowSensor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import com.listener.waterFlowSensor.service.CacheCleaningService;

@RestController
public class CacheCleaningController {
	
	@Autowired
	private CacheCleaningService cacheCleaningService;
	
	private final String TEN_MINUTES_CRON_EXPRESSION = "0 */10 * ? * *";

	@Scheduled(cron = TEN_MINUTES_CRON_EXPRESSION)
	@Async
	@DeleteMapping(value = "/v1/cache/inserted-records")
	public ResponseEntity<String>CleanInsertedRecordsCache() {
		try {
			cacheCleaningService.cleanInsertedRecordsCache();
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<String>("All cache records were deleted successfully", HttpStatus.OK);
	}
}