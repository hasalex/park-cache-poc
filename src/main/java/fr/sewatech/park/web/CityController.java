package fr.sewatech.park.web;

import fr.sewatech.park.dao.CityRepository;
import fr.sewatech.park.data.City;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "city")
public class CityController extends CrudController<City> {

    @Autowired
    public CityController(CityRepository repository) {
        super(repository);
    }

}
