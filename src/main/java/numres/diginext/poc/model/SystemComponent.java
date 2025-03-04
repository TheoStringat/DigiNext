package numres.diginext.poc.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class SystemComponent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;
    private String description;
    private String technology;
    private String version;

    // Correction : mappedBy doit correspondre au nom du champ dans ComponentRelationship
    @OneToMany(mappedBy = "target", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ComponentRelationship> incomingRelationships = new HashSet<>();

    @OneToMany(mappedBy = "source", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ComponentRelationship> outgoingRelationships = new HashSet<>();
}