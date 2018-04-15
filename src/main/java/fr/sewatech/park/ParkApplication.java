package fr.sewatech.park;

import cz.jirutka.spring.embedmongo.EmbeddedMongoFactoryBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.IOException;

@SpringBootApplication
@EnableCaching
@Slf4j
public class ParkApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(ParkApplication.class)
                .run(args);
    }

    @Bean
    public MongoTemplate mongoTemplate() throws IOException {
        return new MongoTemplate(
                new EmbeddedMongoFactoryBean().getObject(),
                "embedded"
        );
    }
}