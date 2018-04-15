package fr.sewatech.park.web;

import fr.sewatech.park.dao.BrandRepository;
import fr.sewatech.park.data.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "brand")
public class BrandController extends CrudController<Brand> {

    @Autowired
    public BrandController(BrandRepository repository) {
        super(repository);
    }

}
