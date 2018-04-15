package fr.sewatech.park.dao;

import fr.sewatech.park.data.City;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

@CacheConfig(cacheNames = City.CACHE_NAME)
public interface CityRepository extends CrudRepository<City, String> {

    @CachePut(key = "#p0.id")
    City save(City entity);

    @Cacheable()
    Optional<City> findById(String id);

    Iterable<City> findAll();

    @CacheEvict(key = "#p0.id")
    void delete(City entity);

}
