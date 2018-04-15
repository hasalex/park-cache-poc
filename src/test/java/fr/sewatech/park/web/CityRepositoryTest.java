package fr.sewatech.park.web;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import fr.sewatech.park.dao.CityRepository;
import fr.sewatech.park.data.City;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RunWith(SpringRunner.class)
public class CityRepositoryTest {

    private static final String CITY_ID = "ccc";
    private static final String CITY_NAME = "City Test";

    @Autowired
    CityRepository cityRepository;

    @Autowired
    CacheManager cacheManager;

    @Autowired
    MongoClient mongoClient;
    @SpyBean
    MongoTemplate mongoTemplate;

    @Before
    public void setUp() {
        MongoDatabase database = mongoClient.getDatabase("embedded");
        database.drop();
        database.createCollection("city");
        MongoCollection<Document> cityCollection = database.getCollection("city");
        cityCollection.insertOne(new Document().append("_id", CITY_ID).append("name", CITY_NAME));

        cacheManager.getCache(City.CACHE_NAME).clear();
    }

    @Test
    public void entity_should_be_in_cache_after_first_findById() {
        // GIVeN

        // WHeN
        Optional<City> optionalCity = cityRepository.findById(CITY_ID);

        // THeN
        City expectedCity = buildCity(CITY_ID, CITY_NAME);
        assertThat(optionalCity)
                .isNotEmpty()
                .contains(expectedCity);
        assertThat(cacheManager.getCache(City.CACHE_NAME).get(CITY_ID).get())
                .isNotNull()
                .isEqualTo(expectedCity);
    }

    @Test
    public void entity_should_not_be_queried_in_database_on_second_findById() {
        // GIVeN
        // ... first call
        cityRepository.findById(CITY_ID);

        // WHeN
        Optional<City> optionalCity = cityRepository.findById(CITY_ID);

        // THeN
        City expectedCity = buildCity(CITY_ID, CITY_NAME);
        assertThat(optionalCity)
                .isNotEmpty()
                .contains(expectedCity);
        verify(mongoTemplate, times(1)).findById(CITY_ID, City.class, "city");
    }

    private City buildCity(String cityId, String cityName) {
        City city = new City();
        city.setId(cityId);
        city.setName(cityName);

        return city;
    }
}