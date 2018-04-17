package fr.sewatech.park.dao;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import fr.sewatech.park.data.City;
import fr.sewatech.park.data.Concession;
import org.bson.Document;
import org.bson.conversions.Bson;
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

import static fr.sewatech.park.dao.DataInitializer.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RunWith(SpringRunner.class)
public class ConcessionRepositoryTest {

    @Autowired
    ConcessionRepository concessionRepository;

    @Autowired
    CacheManager cacheManager;

    @SpyBean
    MongoClient mongoClient;
    @SpyBean
    MongoTemplate mongoTemplate;

    @Before
    public void setUp() {
        DataInitializer.initDatabase(mongoClient);

        DataInitializer.clearCache(cacheManager);
    }

    @Test
    public void concession_should_not_be_in_cache_after_findById() {
        // GIVeN

        // WHeN
        Optional<Concession> optionalConcession = concessionRepository.findById(CONCESSION_ID);

        // THeN
        Concession expectedConcession = buildConcession(CONCESSION_ID, CONCESSION_NAME, buildCity(CITY_ID, CITY_NAME));
        assertThat(optionalConcession)
                .isNotEmpty()
                .contains(expectedConcession);

        assertThat(cacheManager.getCache("concession").get(CONCESSION_ID))
                .isNull();
    }

    @Test
    public void city_should_be_in_cache_after_findById() {
        // GIVeN

        // WHeN
        concessionRepository.findById(CONCESSION_ID);

        // THeN
        City expectedCity = buildCity(CITY_ID, CITY_NAME);
        assertThat(cacheManager.getCache(City.CACHE_NAME).get(CITY_ID))
                .as("Cached city wrapper")
                .isNotNull();
        assertThat(cacheManager.getCache(City.CACHE_NAME).get(CITY_ID).get())
                .as("Cached city")
                .isEqualTo(expectedCity);
    }

    @Test
    public void lazy_city_should_not_be_in_cache_after_findById() {
        // GIVeN

        // WHeN
        concessionRepository.findById(CONCESSION_LAZY_ID);

        // THeN
        assertThat(cacheManager.getCache(City.CACHE_NAME).get(CITY_ID))
                .isNull();
    }

    @Test
    public void city_should_be_queried_in_database_on_first_findById() {
        // GIVeN

        MongoDatabase spiedDatabase = spyDatabase();
        MongoCollection<Document> spiedCollection = spyCollection(spiedDatabase, "city");

        // WHeN
        concessionRepository.findById(CONCESSION_ID);

        // THeN
        verify(mongoTemplate, times(1)).findById(CONCESSION_ID, Concession.class, "concession");
        verify(spiedDatabase, atLeastOnce()).getCollection("city", Document.class);

        verify(spiedCollection, times(1)).find(any(Bson.class));
    }

    @Test
    public void city_should_not_be_queried_in_database_on_second_findById() {
        // GIVeN
        // ... first call
        concessionRepository.findById(CONCESSION_ID);

        MongoDatabase spiedDatabase = spyDatabase();
        MongoCollection<Document> spiedCollection = spyCollection(spiedDatabase, "city");

        // WHeN
        concessionRepository.findById(CONCESSION_ID);

        // THeN
        verify(mongoTemplate, times(2)).findById(CONCESSION_ID, Concession.class, "concession");
        verify(spiedDatabase, times(1)).getCollection("city", Document.class);

        verify(spiedCollection, never()).find(any(Bson.class));
    }

    @Test
    public void lazy_city_should_not_be_queried_in_database_on_findById() {
        // GIVeN
        MongoDatabase spiedDatabase = spyDatabase();
        MongoCollection<Document> spiedCollection = spyCollection(spiedDatabase, "city");

        // WHeN
        Concession concession = concessionRepository.findById(CONCESSION_LAZY_ID)
                .orElseThrow(() ->  new RuntimeException("Concession not found for id " + CONCESSION_LAZY_ID));
//        concessionRepository.findById(CONCESSION_LAZY_ID);

        // THeN
        verify(mongoTemplate, times(1)).findById(CONCESSION_LAZY_ID, Concession.class, "concession");
        verify(spiedCollection, never()).find(any(Bson.class));
    }

    @Test
    public void lazy_city_should_be_queried_in_database_on_get() {
        // GIVeN
        MongoDatabase spiedDatabase = spyDatabase();
        MongoCollection<Document> spiedCollection = spyCollection(spiedDatabase, "city");

        Concession concession = concessionRepository.findById(CONCESSION_LAZY_ID)
                .orElseThrow(() ->  new RuntimeException("Concession not found for id " + CONCESSION_LAZY_ID));

        // WHeN
        concession.getLazyCity().getName();

        // THeN
        verify(mongoTemplate, times(1)).findById(CONCESSION_LAZY_ID, Concession.class, "concession");
        verify(spiedCollection, times(1)).find(any(Bson.class));
    }




    private MongoCollection<Document> spyCollection(MongoDatabase spiedDatabase, String collectionName) {
        MongoCollection<Document> collection = mongoClient.getDatabase("embedded").getCollection(collectionName);
        MongoCollection<Document> spiedCollection = spy(collection);
        when(spiedDatabase.getCollection("city", Document.class)).thenReturn(spiedCollection);
        return spiedCollection;
    }

    private MongoDatabase spyDatabase() {
        MongoDatabase spiedDatabase = spy(mongoClient.getDatabase("embedded"));
        when(mongoClient.getDatabase("embedded")).thenReturn(spiedDatabase);
        return spiedDatabase;
    }
}
