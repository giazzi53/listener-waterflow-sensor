package com.listener.waterFlowSensor.mongoDB;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.listener.waterFlowSensor.domain.WaterFlowSensorDomain;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import lombok.Getter;
import lombok.Setter;

@Repository
@Getter
@Setter
public class MongoDBConnection {
	
	private MongoClient mongoClient;
	
	private MongoDatabase database;
	
	private MongoCollection<Document> collection;
	
	@Value("${CONNECTION_STRING}")
	private String connectionString;
	
	@Value("${DATABASE_NAME}")
	private String databaseName;
	
	@Value("${COLLECTION_NAME}")
	private String collectionName;
	
	public MongoDBConnection() {
	}
	
	public void openConnection() {
		MongoClientURI uri = new MongoClientURI(connectionString);
		this.mongoClient = new MongoClient(uri);
		this.database = mongoClient.getDatabase(databaseName);
		this.collection = database.getCollection(collectionName);
	}
	
	public void closeConnection() {
		this.mongoClient.close();
	}
	
	public void store(WaterFlowSensorDomain domain) {
		Document dataCollected = new Document("flowRate", domain.getFlowRate())
				.append("description", domain.getDescription())
				.append("timestamp", domain.getTimestamp())
				.append("username", domain.getUsername())
				.append("deviceId", domain.getDeviceId())
				.append("weekDay", domain.getWeekDay());
		this.collection.insertOne(dataCollected);
		//this.collection.deleteMany(new Document()); //para deletar todos os documentos da collection
	}
}
