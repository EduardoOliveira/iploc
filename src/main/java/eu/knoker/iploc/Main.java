package eu.knoker.iploc;

import eu.knoker.iploc.actions.EventNotification;
import eu.knoker.iploc.repositories.AccessRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

@SpringBootApplication
@EnableMongoRepositories(basePackageClasses = AccessRepository.class)
@Slf4j
public class Main {

    Sinks.Many<EventNotification> sink = Sinks.unsafe().many().replay().latest();

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public Sinks.Many<EventNotification> eventChannel() {
        return sink;
    }

    @Bean
    public Flux<EventNotification> eventNotifications() {
        return sink.asFlux().subscribeOn(Schedulers.boundedElastic());
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*");
            }
        };
    }
}
