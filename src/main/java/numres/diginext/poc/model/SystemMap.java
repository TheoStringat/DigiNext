package numres.diginext.poc.model;

import jakarta.persistence.*;
import lombok.Data;
import numres.diginext.poc.model.SystemComponent;
import numres.diginext.poc.model.ComponentRelationship;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class SystemMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String createdBy;
    private String createdDate;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SystemComponent> components = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ComponentRelationship> relationships = new HashSet<>();

    private String plantUmlDiagram;
}
