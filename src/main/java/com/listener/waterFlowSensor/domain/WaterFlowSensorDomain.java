package com.listener.waterFlowSensor.domain;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class WaterFlowSensorDomain {

	private double flowRate;
	private String description;
	private String timestamp;
	private String username;
	private String deviceId;
	private String weekDay;
	
}