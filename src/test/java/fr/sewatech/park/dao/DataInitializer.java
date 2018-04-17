package fr.sewatech.park.dao;

import com.mongodb.DBRef;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import fr.sewatech.park.data.City;
import fr.sewatech.park.data.Concession;
import org.bson.Document;
import org.springframework.cache.CacheManager;

class DataInitializer {

    static final String CITY_ID = "city0";
    static final String CITY_NAME = "City Test";

    static final String CONCESSION_ID = "conc0";
    static final String CONCESSION_NAME = "Concession Test";

    static final String CONCESSION_LAZY_ID = "conc0-lazy";
    static final String CONCESSION_LAZY_NAME = "Concession Test (with lazy city)";

    static void initDatabase(MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("embedded");
        database.drop();

        database.createCollection("city");
        MongoCollection<Document> cityCollection = database.getCollection("city");
        Document cityDocument = new Document()
                .append("_id", CITY_ID)
                .append("name", CITY_NAME);
        cityCollection.insertOne(cityDocument);

        database.createCollection("concession");
        MongoCollection<Document> concessionCollection = database.getCollection("concession");
        Document concessionDocument = new Document()
                .append("_id", CONCESSION_ID)
                .append("name", CONCESSION_NAME)
                .append("city", new DBRef("city", CITY_ID));
        concessionCollection.insertOne(concessionDocument);
        Document lazyConcessionDocument = new Document()
                .append("_id", CONCESSION_LAZY_ID)
                .append("name", CONCESSION_LAZY_NAME)
                .append("lazy_city", new DBRef("city", CITY_ID));
        concessionCollection.insertOne(lazyConcessionDocument);
    }

    static City buildCity(String id, String name) {
        City city = new City();
        city.setId(id);
        city.setName(name);

        return city;
    }

    static Concession buildConcession(String id, String name, City city) {
        Concession concession = new Concession();
        concession.setId(id);
        concession.setName(name);
        concession.setCity(city);

        return concession;
    }

    static void clearCache(CacheManager cacheManager) {
        cacheManager.getCacheNames()
                .forEach(name -> cacheManager.getCache(name).clear());
    }
}
