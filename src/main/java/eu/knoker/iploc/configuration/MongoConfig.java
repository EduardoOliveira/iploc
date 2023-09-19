package eu.knoker.iploc.configuration;

import com.fasterxml.jackson.databind.Module;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonModule;
import org.springframework.data.web.config.SpringDataJacksonModules;


@Configuration
public class MongoConfig implements SpringDataJacksonModules {

    @Value("${spring.data.mongodb.uri}")
    String mongoUri;

    @Value("${spring.data.mongodb.database}")
    String databaseName;


    public @Bean MongoClient reactiveMongoClient() {
        return MongoClients.create(mongoUri);
    }

    public @Bean ReactiveMongoTemplate reactiveMongoTemplate() {
        return new ReactiveMongoTemplate(reactiveMongoClient(), databaseName);
    }

    @Bean
    public Module geoJsonSerializers() {
        return GeoJsonModule.serializers();
    }

}