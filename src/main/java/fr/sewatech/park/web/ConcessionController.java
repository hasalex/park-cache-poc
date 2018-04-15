package fr.sewatech.park.web;

import fr.sewatech.park.dao.ConcessionRepository;
import fr.sewatech.park.data.Concession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "concession")
public class ConcessionController extends CrudController<Concession> {

    @Autowired
    public ConcessionController(ConcessionRepository repository) {
        super(repository);
    }

}
