package com.listener.waterFlowSensor.DTO;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "InsertedCacheRecordCollection")
public class InsertedCacheRecordDTO {
	
	private String username;
	private String deviceId;
	private String timestamp;
	private long millisSinceConnected;
	
}
