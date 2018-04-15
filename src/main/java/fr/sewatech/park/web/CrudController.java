package fr.sewatech.park.web;

import fr.sewatech.park.data.AbstractDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

abstract class CrudController<T extends AbstractDocument> {

    private final CrudRepository<T, String> repository;

    CrudController(CrudRepository<T, String> repository) {
        this.repository = repository;
    }

    @Autowired
    CacheManager cacheManager;

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<T> findAll() {
        return repository.findAll();
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<T> findOne(@PathVariable String id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @RequestMapping(method = RequestMethod.PUT)
    public T update(@RequestBody T entity) {
        return repository.save(entity);
    }

    @RequestMapping(method = RequestMethod.POST)
    public T create(@RequestBody T entity) {
        return repository.save(entity);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void kill(@RequestBody T entity) {
        repository.delete(entity);
    }

}
