package fr.sewatech.park.data;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class City extends AbstractDocument {
    public static final String CACHE_NAME = "city";

    private String name;

}
