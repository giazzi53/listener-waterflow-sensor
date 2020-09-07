package com.listener.waterFlowSensor.controller;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.listener.waterFlowSensor.DAO.DeviceDAO;
import com.listener.waterFlowSensor.DAO.InsertedCacheRecordDAO;
import com.listener.waterFlowSensor.DTO.DeviceDTO;
import com.listener.waterFlowSensor.DTO.InsertedCacheRecordDTO;

@RestController
public class WaterFlowSensorController {

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
	private DeviceDTO deviceDTO;

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private DeviceDAO deviceDAO;
	
	@Autowired
	private InsertedCacheRecordDAO insertedCacheRecordDAO;

	private final Logger LOGGER = LoggerFactory.getLogger(WaterFlowSensorController.class);

	@Scheduled(fixedRate = 30000)
	@Async
	@GetMapping(value = "/getData")
	public void getData() {
	    DecimalFormat df2 = new DecimalFormat("#.##");
		double flowRate = Double.parseDouble(sendGETRequest(waterFlowURL));
		this.deviceDTO.setFlowRate(Double.parseDouble(df2.format(flowRate)));
		
		String description = sendGETRequest(descriptionURL);
		this.deviceDTO.setDescription(description);
		
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.deviceDTO.setTimestamp(dateFormat.format(date));
		
		long millisSinceConnected = Long.parseLong(sendGETRequest(millisSinceConnectedURL));

		String username = sendGETRequest(userURL);
		this.deviceDTO.setUsername(username);

		String deviceId = sendGETRequest(deviceIdURL);
		this.deviceDTO.setDeviceId(deviceId);
		
		String weekDay = LocalDate.now().getDayOfWeek().name();
		this.deviceDTO.setWeekDay(weekDay);
								
		try {
			if(!this.insertedCacheRecordDAO.existsByUsernameAndDeviceIdAndMillisSinceConnected(this.deviceDTO.getUsername(),
					this.deviceDTO.getDeviceId(), millisSinceConnected)) {
				Gson gson = new Gson();
				LOGGER.info("!!! Inserindo " + gson.toJson(this.deviceDTO) + " no MongoDB !!!!");
				this.deviceDAO.insert(this.deviceDTO);
				InsertedCacheRecordDTO cacheRecord = new InsertedCacheRecordDTO(this.deviceDTO.getUsername(),
						this.deviceDTO.getDeviceId(), this.deviceDTO.getTimestamp(),
						millisSinceConnected);
				this.insertedCacheRecordDAO.insert(cacheRecord);
			}
		} catch (Exception e) {
			LOGGER.error("Ocorreu um erro ao inserir no MongoDB", e);
		}
	}

	private String sendGETRequest(String URL) {
		String responseString = null;
		
		try {
			responseString = restTemplate.getForObject(URL, String.class);

			if (responseString.contains("[") && responseString.contains("]")) { // removendo caracteres [ e ]
				responseString = responseString.replace("[", "").replace("]", "");
			}

			if (responseString.contains("\"")) { // removendo aspas, pois o Mongo ja as insere, ficando duplicado
				responseString = responseString.replace("\"", "");
			}
			
		} catch (Exception e) {
			LOGGER.error("Ocorreu um erro ao mandar a requisição GET");
		}
		
		return responseString;
	}
	
	@PutMapping(value = "/adjustWeeklyData")
	public void adjustWeeklyData() throws ParseException {
		List<DeviceDTO> allDeviceRecords = deviceDAO.findAll();
		
		for(DeviceDTO device : allDeviceRecords) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			Date timestamp = sdf.parse(device.getTimestamp());
			
			Date newTimestamp = DateUtils.addDays(timestamp, 7);
			
			String newFormattedTimestamp = sdf.format(newTimestamp);
			
			device.setTimestamp(newFormattedTimestamp);
			deviceDAO.save(device);
		}
	}
}