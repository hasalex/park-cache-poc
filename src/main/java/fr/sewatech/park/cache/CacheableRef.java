package fr.sewatech.park.cache;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheableRef {
    String cacheName();

}
