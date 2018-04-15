package fr.sewatech.park.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "cache")
public class CacheController {

    private final CacheManager cacheManager;

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") // Avoid wrong warning for cacheManager
    public CacheController(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @RequestMapping(path = "/{name}", method = RequestMethod.GET)
    public void clear(@PathVariable("name") String name) {
        cacheManager.getCache(name)
                .clear();
    }
}
