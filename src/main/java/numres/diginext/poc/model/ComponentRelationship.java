package numres.diginext.poc.model;

import jakarta.persistence.*;
import lombok.Data;
import numres.diginext.poc.model.SystemComponent;

@Entity
@Data
public class ComponentRelationship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private SystemComponent source;

    @ManyToOne
    private SystemComponent target;

    private String type; // DEPENDS_ON, COMMUNICATES_WITH, etc.
    private String description;
}
