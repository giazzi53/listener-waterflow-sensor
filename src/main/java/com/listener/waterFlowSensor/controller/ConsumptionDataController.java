package com.listener.waterFlowSensor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.listener.waterFlowSensor.DTO.DeviceDTO;
import com.listener.waterFlowSensor.service.ConsumptionDataService;

import lombok.extern.java.Log;

@Log
@RestController
public class ConsumptionDataController {
	
	@Autowired
	private ConsumptionDataService consumptionDataService;

	@Scheduled(fixedRate = 30000)
	@Async
	@GetMapping(value = "/get-consumption-data")
	public ResponseEntity<?> getConsumptionData() {
		DeviceDTO deviceDTO = null;
		
		try {
			log.info("Sending new request to get collected data");
			
			deviceDTO = this.consumptionDataService.createConsumptionObject();
						
			this.consumptionDataService.insertIntoDB(deviceDTO);
			
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<DeviceDTO>(deviceDTO, HttpStatus.OK);
	}
	
	@PutMapping(value = "/increment-all-records-by-one-week")
	public ResponseEntity<String>incrementAllRecordsByOneWeek() {
		try {
			this.consumptionDataService.incrementAllRecordsByOneWeek();
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<String>("Updated all records successfully", HttpStatus.OK);
	}
}