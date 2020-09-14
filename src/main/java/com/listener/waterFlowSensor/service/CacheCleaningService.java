package com.listener.waterFlowSensor.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.listener.waterFlowSensor.DAO.InsertedCacheRecordDAO;

import lombok.extern.java.Log;

@Log
public class CacheCleaningService {
	
	@Autowired
	private InsertedCacheRecordDAO insertedCacheRecordDAO;
	
	public void cleanInsertedRecordsCache() throws Exception {
		log.info("Cleaning all cache records from collection InsertedCacheRecordCollection");
		
		try {
			insertedCacheRecordDAO.deleteAll();
			log.info("All cache records were deleted successfully");
		} catch(Exception e) {
			throw new Exception("There was an error while deleting cache records " + e); 
		}
	}
}