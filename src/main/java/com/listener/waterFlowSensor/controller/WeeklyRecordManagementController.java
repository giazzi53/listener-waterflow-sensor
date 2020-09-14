package com.listener.waterFlowSensor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.listener.waterFlowSensor.service.WeeklyRecordManagementService;

@RestController
@RequestMapping("/v1/records")
public class WeeklyRecordManagementController {
	
	@Autowired
	private WeeklyRecordManagementService weeklyRecordManagementService;

	@PutMapping(value = "/weekly-data")
	public ResponseEntity<String>incrementAllRecordsByOneWeek() {
		try {
			weeklyRecordManagementService.incrementAllRecordsByOneWeek();
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<String>("Updated all records successfully", HttpStatus.OK);
	}
}