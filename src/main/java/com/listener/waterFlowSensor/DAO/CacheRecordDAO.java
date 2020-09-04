package com.listener.waterFlowSensor.DAO;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.listener.waterFlowSensor.DTO.CacheRecordDTO;

@Repository
public interface CacheRecordDAO extends MongoRepository<CacheRecordDTO, String>{
		
	boolean existsByUsernameAndDeviceIdAndMillisSinceConnected(String username, String deviceId, long millisSinceConnected);
}
