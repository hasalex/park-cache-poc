package fr.sewatech.park.dao;

import com.mongodb.MongoClient;
import fr.sewatech.park.data.City;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static fr.sewatech.park.dao.DataInitializer.CITY_ID;
import static fr.sewatech.park.dao.DataInitializer.CITY_NAME;
import static fr.sewatech.park.dao.DataInitializer.buildCity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RunWith(SpringRunner.class)
public class CityRepositoryTest {

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
        DataInitializer.initDatabase(mongoClient);

        DataInitializer.clearCache(cacheManager);
    }

    @Test
    public void city_should_be_in_cache_after_first_findById() {
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
    public void database_should_not_be_queried_on_second_findById() {
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

}