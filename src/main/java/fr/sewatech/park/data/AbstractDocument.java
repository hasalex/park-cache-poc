package fr.sewatech.park.data;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(language = "fr")
public abstract class AbstractDocument {

    @Id
    private String id;

}
