package com.listener.waterFlowSensor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.listener.waterFlowSensor.service.WeeklyRecordManagementService;

@RestController
public class WeeklyRecordManagementController {
	
	@Autowired
	private WeeklyRecordManagementService weeklyRecordManagementService;

	@Async
	@PutMapping(value = "/increment-all-records-by-one-week")
	public ResponseEntity<String>incrementAllRecordsByOneWeek() {
		try {
			weeklyRecordManagementService.incrementAllRecordsByOneWeek();
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<String>("Updated all records successfully", HttpStatus.OK);
	}
}