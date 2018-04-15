package fr.sewatech.park.dao;

import fr.sewatech.park.data.Brand;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

@CacheConfig(cacheNames = "brand")
public interface BrandRepository extends CrudRepository<Brand, String> {

    @CachePut(key = "#p0.id")
    Brand save(Brand entity);

    @Cacheable()
    Optional<Brand> findById(String id);

    Iterable<Brand> findAll();

    @CacheEvict(key = "#p0.id")
    void delete(Brand entity);

}
