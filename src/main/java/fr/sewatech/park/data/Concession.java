package fr.sewatech.park.data;

import fr.sewatech.park.cache.CacheableRef;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class Concession extends AbstractDocument {

    private String name;

    @DBRef @CacheableRef(cacheName = "city")
    private City city;

    @DBRef
    private Set<Brand> brands = new HashSet<>();

}
