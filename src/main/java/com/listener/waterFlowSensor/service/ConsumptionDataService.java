package com.listener.waterFlowSensor.service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.listener.waterFlowSensor.DAO.DeviceDAO;
import com.listener.waterFlowSensor.DAO.InsertedCacheRecordDAO;
import com.listener.waterFlowSensor.DTO.DeviceDTO;
import com.listener.waterFlowSensor.DTO.InsertedCacheRecordDTO;

import lombok.extern.java.Log;

@Log
public class ConsumptionDataService {

	@Value("${WATERFLOW_URL}")
	private String waterFlowURL;

	@Value("${USER_URL}")
	private String userURL;

	@Value("${DEVICEID_URL}")
	private String deviceIdURL;

	@Value("${DESCRIPTION_URL}")
	private String descriptionURL;

	@Value("${MILLIS_SINCE_CONNECTED_URL}")
	private String millisSinceConnectedURL;

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private Gson gson;

	@Autowired
	private DeviceDAO deviceDAO;

	@Autowired
	private InsertedCacheRecordDAO insertedCacheRecordDAO;

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private final DecimalFormat decimalFormat = new DecimalFormat("#.##");
	
	private final int DAYS_TO_BE_ADDED = 7;

	public DeviceDTO createConsumptionObject() throws Exception {
		DeviceDTO deviceDTO = null;

		try {
			String flowRateString = sendGETRequest(this.waterFlowURL);
			double flowRate = formatToTwoDecimalPlaces(flowRateString);

			String description = sendGETRequest(this.descriptionURL);

			String timestamp = generateTimestamp();

			String username = sendGETRequest(this.userURL);

			String deviceId = sendGETRequest(this.deviceIdURL);

			String weekDay = getWeekDay();

			deviceDTO = new DeviceDTO(flowRate, description, timestamp, username, deviceId, weekDay);

		} catch (Exception e) {
			log.severe("Error while generating consuimption object " + e);
			throw new Exception(e);
		}

		return deviceDTO;
	}

	private String sendGETRequest(String URL) throws Exception {
		String responseString = this.restTemplate.getForObject(URL, String.class);

		if (responseString == null) {
			throw new Exception("There was an error while sending a GET request to endpoint " + URL);
		}

		return formatResponseString(responseString);
	}

	private String formatResponseString(String responseString) throws Exception {
		try {
			responseString = removeBrackets(responseString);

			responseString = removeDoubleQuotes(responseString);

		} catch (Exception e) {
			throw new Exception("There was an error while formatting response " + responseString);
		}

		return responseString;
	}

	private String removeBrackets(String responseString) {
		if (responseString.contains("[") && responseString.contains("]")) {
			responseString = responseString.replace("[", "").replace("]", "");
		}

		return responseString;
	}

	private String removeDoubleQuotes(String responseString) {
		// removing double quotes, since MongoDB already
		// inserts them, making it duplicate
		if (responseString.contains("\"")) {
			responseString = responseString.replace("\"", "");
		}

		return responseString;
	}

	private double formatToTwoDecimalPlaces(String flowRateString) throws Exception {
		try {
			return Double.parseDouble(this.decimalFormat.format(flowRateString));
		} catch (Exception e) {
			throw new Exception("There was an error while formatting value " +
								flowRateString + " to two decimal places");
		}
	}

	private String generateTimestamp() throws Exception {
		try {
			Date date = new Date();
			return this.dateFormat.format(date);
		} catch (Exception e) {
			throw new Exception("There was an error while generating timestamp");
		}
	}

	private String getWeekDay() throws Exception {
		try {
			return LocalDate.now().getDayOfWeek().name();
		} catch (Exception e) {
			throw new Exception("There was an error while getting weekday");
		}
	}
	
	public void insertIntoDB(DeviceDTO deviceDTO) throws Exception {
		long millisSinceConnected = Long.parseLong(sendGETRequest(this.millisSinceConnectedURL));

		if(!this.insertedCacheRecordDAO
				.existsByUsernameAndDeviceIdAndMillisSinceConnected(
				deviceDTO.getUsername(), deviceDTO.getDeviceId(), millisSinceConnected)) {
			
			this.insertIntoDeviceCollection(deviceDTO);
			
			InsertedCacheRecordDTO insertedCacheRecordDTO = new InsertedCacheRecordDTO(
					deviceDTO.getUsername(), deviceDTO.getDeviceId(),
					deviceDTO.getTimestamp(), millisSinceConnected);
			
			this.insertIntoCacheRecordCollection(insertedCacheRecordDTO);
		}
	}
	
	private void insertIntoDeviceCollection(DeviceDTO deviceDTO) throws Exception {
		try {
			log.info("Inserting " + this.gson.toJson(deviceDTO) + " into DeviceCollection");
			
			this.deviceDAO.insert(deviceDTO);
			
			log.info("Device object inserted successfully");

		} catch(Exception e) {
			log.severe("There was an error while inserting " + this.gson.toJson(deviceDTO)
					   + " into DeviceCollection " + e);
			throw new Exception(e);
		}
	}
	
	private void insertIntoCacheRecordCollection(InsertedCacheRecordDTO insertedCacheRecordDTO) throws Exception {
		try {
			log.info("Inserting " + this.gson.toJson(insertedCacheRecordDTO) +
					 " into InsertedCacheRecordCollection");
			
			this.insertedCacheRecordDAO.insert(insertedCacheRecordDTO);
			
			log.info("Cache record inserted successfully");

		} catch (Exception e) {
			log.severe("There was an error while inserting " + this.gson.toJson(insertedCacheRecordDTO)
			+ " into InsertedCacheRecordCollection " + e);
			throw new Exception(e);
		}
	}
	
	public void incrementAllRecordsByOneWeek() throws Exception {
		log.info("Incrementing all records by one week");
		
		List<DeviceDTO> allDeviceRecords = this.deviceDAO.findAll();
		
		log.info("Total records to be updated: " + allDeviceRecords.size());

		int count = 1;
		
		for(DeviceDTO device : allDeviceRecords) {
			Date oldTimestamp = this.dateFormat.parse(device.getTimestamp());
						
			String newTimestamp = this.getNewTimestamp(oldTimestamp);
			
			log.info("Record #" + count + " - Old object " + this.gson.toJson(device));
			
			device.setTimestamp(newTimestamp);
									
			this.insertIntoDeviceCollection(device);
						
			count++;
		}
	}
	
	private String getNewTimestamp(Date oldTimestamp) throws Exception {
		try {
			Date newTimestamp = DateUtils.addDays(oldTimestamp, DAYS_TO_BE_ADDED);
			
			return this.dateFormat.format(newTimestamp);
			
		} catch (Exception e) {
			throw new Error("There was an error while getting the new timestamp to be added " + e);
		}
	}
}
