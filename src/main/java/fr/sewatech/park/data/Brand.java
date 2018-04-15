package fr.sewatech.park.data;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Brand extends AbstractDocument {

    private String name;

}
