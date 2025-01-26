package com.unipi.ItineraJava.configuration;

import com.mongodb.WriteConcern;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteConcernResolver;

@Configuration
public class MongoConfig {

    @Bean
    public WriteConcernResolver writeConcernResolver() {
        // Specifica il Write Concern desiderato, ad esempio "MAJORITY"
        return action -> WriteConcern.MAJORITY;
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoTemplate mongoTemplate) {
        mongoTemplate.setWriteConcernResolver(writeConcernResolver());
        return mongoTemplate;
    }
}
