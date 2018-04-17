package fr.sewatech.park.data;

import fr.sewatech.park.cache.CacheableRef;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class Concession extends AbstractDocument {

    private String name;

    @DBRef @CacheableRef(cacheName = City.CACHE_NAME)
    private City city;

    @DBRef(lazy = true) @CacheableRef(cacheName = City.CACHE_NAME) @Field("lazy_city")
    private City lazyCity;

    @DBRef
    private Set<Brand> brands = new HashSet<>();

}
