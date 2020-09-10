package com.listener.waterFlowSensor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import com.listener.waterFlowSensor.DTO.DeviceDTO;
import com.listener.waterFlowSensor.service.ConsumptionDataService;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class ListenerWaterflowSensorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ListenerWaterflowSensorApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	@Bean
	public DeviceDTO deviceDTO() {
		return new DeviceDTO();
	}
	
	@Bean
	public ConsumptionDataService consumptionDataService() {
		return new ConsumptionDataService();
	}
	
}