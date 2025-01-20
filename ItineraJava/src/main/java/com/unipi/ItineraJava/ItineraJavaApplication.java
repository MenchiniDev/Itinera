package com.unipi.ItineraJava;

import com.mongodb.client.MongoClient;
import com.unipi.ItineraJava.graphdb.CreateGraphDatabase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

import static com.unipi.ItineraJava.documentDb.MongoDBUploader.getMongoConnection;

@SpringBootApplication
public class ItineraJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItineraJavaApplication.class, args);
	}
}
