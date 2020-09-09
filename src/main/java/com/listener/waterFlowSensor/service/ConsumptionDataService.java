package com.listener.waterFlowSensor.service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

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
	private DeviceDAO deviceDAO;

	@Autowired
	private InsertedCacheRecordDAO insertedCacheRecordDAO;

	public DeviceDTO createConsumptionObject() throws Exception {
		DeviceDTO deviceDTO = null;

		try {
			String flowRateString = sendGETRequest(waterFlowURL);
			double flowRate = formatToTwoDecimalPlaces(flowRateString);
			// double flowRate =
			// formatToTwoDecimalPlaces(Double.parseDouble(flowRateString));

			String description = sendGETRequest(descriptionURL);

			String timestamp = generateTimestamp();

			String username = sendGETRequest(userURL);

			String deviceId = sendGETRequest(deviceIdURL);

			String weekDay = getWeekDay();

			deviceDTO = new DeviceDTO(flowRate, description, timestamp, username, deviceId, weekDay);

		} catch (Exception e) {
			log.severe("Error while generating consuimption object " + e);
			
			throw new Exception(e);
		}

		return deviceDTO;
	}

	private String sendGETRequest(String URL) throws Exception {
		String responseString = restTemplate.getForObject(URL, String.class);

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
			DecimalFormat df2 = new DecimalFormat("#.##");

			return Double.parseDouble(df2.format(flowRateString));

		} catch (Exception e) {
			throw new Exception("There was an error while formatting value " +
								flowRateString + " to two decimal places");
		}
	}

	private String generateTimestamp() throws Exception {
		try {
			Date date = new Date();

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			return dateFormat.format(date);

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
		long millisSinceConnected = Long.parseLong(sendGETRequest(millisSinceConnectedURL));

		try {
			if(!this.insertedCacheRecordDAO.existsByUsernameAndDeviceIdAndMillisSinceConnected(deviceDTO.getUsername(),
					deviceDTO.getDeviceId(), millisSinceConnected)) {
				Gson gson = new Gson();
				log.info("!!! Inserindo " + gson.toJson(deviceDTO) + " no MongoDB !!!!");
				this.deviceDAO.insert(deviceDTO);
				InsertedCacheRecordDTO cacheRecord = new InsertedCacheRecordDTO(deviceDTO.getUsername(),
						deviceDTO.getDeviceId(), deviceDTO.getTimestamp(),
						millisSinceConnected);
				this.insertedCacheRecordDAO.insert(cacheRecord);
			}
		} catch (Exception e) {
			log.severe("Ocorreu um erro ao inserir no MongoDB " + e);
		}
	}
}
