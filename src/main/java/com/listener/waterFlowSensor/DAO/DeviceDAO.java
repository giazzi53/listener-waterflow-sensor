package com.listener.waterFlowSensor.DAO;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.listener.waterFlowSensor.DTO.DeviceDTO;

@Repository
public interface DeviceDAO extends MongoRepository<DeviceDTO, String>{
		
}
