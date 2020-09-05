package com.listener.waterFlowSensor.DAO;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.listener.waterFlowSensor.DTO.InsertedCacheRecordDTO;

@Repository
public interface InsertedCacheRecordDAO extends MongoRepository<InsertedCacheRecordDTO, String> {

	boolean existsByUsernameAndDeviceIdAndMillisSinceConnected(String username, String deviceId,
			long millisSinceConnected);
}
