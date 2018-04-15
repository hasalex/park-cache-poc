package fr.sewatech.park.dao;

import fr.sewatech.park.data.Concession;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface ConcessionRepository extends CrudRepository<Concession, String> {

    Set<Concession> findAll();

}
