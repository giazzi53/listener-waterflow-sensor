package com.listener.waterFlowSensor.DTO;

import javax.validation.constraints.NotBlank;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "DeviceCollection")
@JsonInclude(Include.NON_NULL)
public class DeviceDTO {
	
//	private String _id;
	@NotBlank
	private double flowRate;
	@NotBlank
	private String description;
	@NotBlank
	private String timestamp;
	@NotBlank	
	private String username;
	@NotBlank
	private String deviceId;
	@NotBlank
	private String weekDay;

}
