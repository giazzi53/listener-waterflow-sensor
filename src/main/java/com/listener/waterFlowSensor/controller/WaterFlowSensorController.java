package com.listener.waterFlowSensor.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.listener.waterFlowSensor.domain.WaterFlowSensorDomain;
import com.listener.waterFlowSensor.mongoDB.MongoDBConnection;

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

	@Autowired
	private WaterFlowSensorDomain domain;
	
	@Autowired
	private MongoDBConnection mongoDB;

	@Autowired
	private RestTemplate restTemplate;

	private final Logger LOGGER = LoggerFactory.getLogger(WaterFlowSensorController.class);

	@Scheduled(fixedRate = 30000)
	@Async
	@GetMapping(value = "/getData")
	public void getData() {
		this.mongoDB.openConnection();

		double flowRate = Double.parseDouble(sendGETRequest(waterFlowURL));
		this.domain.setFlowRate(flowRate);
		
		String description = sendGETRequest(descriptionURL);
		this.domain.setDescription(description);
		
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.domain.setTimestamp(dateFormat.format(date));

		String username = sendGETRequest(userURL);
		this.domain.setUsername(username);

		String deviceId = sendGETRequest(deviceIdURL);
		this.domain.setDeviceId(deviceId);
		
		String weekDay = LocalDate.now().getDayOfWeek().name();
		this.domain.setWeekDay(weekDay);
				
		try {
			Gson gson = new Gson();
			LOGGER.info("!!! Inserindo " + gson.toJson(this.domain) + " no MongoDB !!!!");
			this.mongoDB.store(this.domain);
		} catch (Exception e) {
			LOGGER.error("Ocorreu um erro ao inserir no MongoDB", e);
		}

		this.mongoDB.closeConnection();
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
}