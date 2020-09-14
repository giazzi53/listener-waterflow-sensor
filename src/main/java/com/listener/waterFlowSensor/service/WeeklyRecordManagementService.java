package com.listener.waterFlowSensor.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.listener.waterFlowSensor.DAO.DeviceDAO;
import com.listener.waterFlowSensor.DTO.DeviceDTO;

import lombok.extern.java.Log;

@Log
public class WeeklyRecordManagementService {
	
	@Autowired
	private DeviceDAO deviceDAO;
	
	@Autowired
	private Gson gson;
	
	@Autowired
	private ConsumptionDataService consumptionDataService;
		
	private final int DAYS_TO_BE_ADDED = 7;
	
	public void incrementAllRecordsByOneWeek() throws Exception {
		log.info("Incrementing all records by one week");
		
		List<DeviceDTO> allDeviceRecords = this.deviceDAO.findAll();
		
		log.info("Total records to be updated: " + allDeviceRecords.size());

		int count = 1;
		
		for(DeviceDTO device : allDeviceRecords) {
			Date oldTimestamp = consumptionDataService.getDateFormat().parse(device.getTimestamp());
						
			String newTimestamp = this.getNewTimestamp(oldTimestamp);
			
			log.info("Record #" + count + " - Old object " + this.gson.toJson(device));
			
			device.setTimestamp(newTimestamp);
									
			consumptionDataService.insertIntoDeviceCollection(device);
						
			count++;
		}
	}
	
	private String getNewTimestamp(Date oldTimestamp) throws Exception {
		try {
			Date newTimestamp = DateUtils.addDays(oldTimestamp, DAYS_TO_BE_ADDED);
			
			return consumptionDataService.getDateFormat().format(newTimestamp);
			
		} catch (Exception e) {
			throw new Error("There was an error while getting the new timestamp to be added " + e);
		}
	}
}
