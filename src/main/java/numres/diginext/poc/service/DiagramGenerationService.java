package numres.diginext.poc.service;

import org.springframework.stereotype.Service;
import numres.diginext.poc.model.SystemMap;
import numres.diginext.poc.model.SystemComponent;
import numres.diginext.poc.model.ComponentRelationship;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class DiagramGenerationService {

    /**
     * Génère un diagramme PlantUML à partir d'une cartographie de système
     * @param systemMap La cartographie du système à représenter
     * @return Le code PlantUML généré
     */
    public String generatePlantUML(SystemMap systemMap) {
        StringBuilder plantUml = new StringBuilder();

        // En-tête du diagramme avec configuration avancée
        plantUml.append("@startuml\n");
        plantUml.append("!theme cerulean\n");
        plantUml.append("skinparam componentStyle uml2\n");
        plantUml.append("skinparam backgroundColor white\n");
        plantUml.append("skinparam handwritten false\n");
        plantUml.append("skinparam defaultTextAlignment center\n");

        // Titre et légende
        plantUml.append("title ").append(systemMap.getName()).append(" - Cartographie du SI\n\n");

        // Définition des styles personnalisés pour les différents types de composants
        configureSkinParams(plantUml);

        // Création d'une map pour stocker les identifiants des composants
        Map<SystemComponent, String> componentIds = new HashMap<>();

        // Définition des composants avec styles adaptés selon leur type
        defineComponents(systemMap, plantUml, componentIds);

        plantUml.append("\n");

        // Définition des relations entre composants
        defineRelationships(systemMap, plantUml, componentIds);

        // Légende
        addLegend(plantUml);

        // Fin du diagramme
        plantUml.append("@enduml");

        return plantUml.toString();
    }

    /**
     * Configure les paramètres d'apparence du diagramme
     */
    private void configureSkinParams(StringBuilder plantUml) {
        plantUml.append("' Configuration des styles pour les différents types de composants\n");
        plantUml.append("skinparam component {\n");
        plantUml.append("  BackgroundColor<<Base de données>> LightBlue\n");
        plantUml.append("  BackgroundColor<<Serveur>> LightGreen\n");
        plantUml.append("  BackgroundColor<<Application>> LightYellow\n");
        plantUml.append("  BackgroundColor<<SaaS>> #CCCCFF\n");
        plantUml.append("  BackgroundColor<<Agent>> #CCFFCC\n");
        plantUml.append("  BackgroundColor<<Technologie>> #FFFFCC\n");
        plantUml.append("  BackgroundColor<<Système>> #FFCCCC\n");
        plantUml.append("  BorderColor Black\n");
        plantUml.append("  FontSize 12\n");
        plantUml.append("}\n\n");

        plantUml.append("skinparam database {\n");
        plantUml.append("  BackgroundColor LightBlue\n");
        plantUml.append("  BorderColor Blue\n");
        plantUml.append("}\n\n");

        plantUml.append("skinparam node {\n");
        plantUml.append("  BackgroundColor LightGreen\n");
        plantUml.append("  BorderColor Green\n");
        plantUml.append("}\n\n");

        plantUml.append("skinparam rectangle {\n");
        plantUml.append("  BackgroundColor LightYellow\n");
        plantUml.append("  BorderColor Orange\n");
        plantUml.append("}\n\n");

        plantUml.append("skinparam arrow {\n");
        plantUml.append("  Color Black\n");
        plantUml.append("  FontSize 10\n");
        plantUml.append("}\n\n");
    }

    private void defineComponents(SystemMap systemMap, StringBuilder plantUml, Map<SystemComponent, String> componentIds) {
        plantUml.append("' Définition des composants du système\n");

        for (SystemComponent component : systemMap.getComponents()) {
            String type = component.getType() != null ? component.getType().toUpperCase() : "UNKNOWN";
            String componentId = "comp_" + UUID.randomUUID().toString().substring(0, 8);
            componentIds.put(component, componentId);

            // Sélection du style de composant en fonction du type
            switch (type) {
                case "DATABASE":
                case "BASE DE DONNÉES":
                    plantUml.append("database \"").append(component.getName()).append("\" as ")
                            .append(componentId).append(" <<Base de données>>\n");
                    break;
                case "SERVER":
                case "SERVEUR":
                    plantUml.append("node \"").append(component.getName()).append("\" as ")
                            .append(componentId).append(" <<Serveur>>\n");
                    break;
                case "APPLICATION":
                case "APP":
                    plantUml.append("rectangle \"").append(component.getName()).append("\" as ")
                            .append(componentId).append(" <<Application>>\n");
                    break;
                case "SAAS":
                    plantUml.append("component \"").append(component.getName()).append("\" as ")
                            .append(componentId).append(" <<SaaS>>\n");
                    break;
                default:
                    plantUml.append("component \"").append(component.getName()).append("\" as ")
                            .append(componentId).append(" <<Système>>\n");
            }

            // Ajouter une description sous forme de note si disponible
            if (component.getDescription() != null && !component.getDescription().isEmpty()) {
                plantUml.append("note right of ").append(componentId)
                        .append(" : ").append(component.getDescription()).append("\n");
            }
        }
    }

    private String getComponentStereotype(String type) {
        switch (type.toUpperCase()) {
            case "DATABASE":
            case "BASE DE DONNÉES":
                return "<<Base de données>>";
            case "SERVER":
            case "SERVEUR":
                return "<<Serveur>>";
            case "APPLICATION":
            case "APP":
                return "<<Application>>";
            case "SAAS":
                return "<<SaaS>>";
            case "AGENT":
                return "<<Agent>>";
            case "TECHNOLOGY":
                return "<<Technologie>>";
            default:
                return "<<Système>>";
        }
    }

    private void defineRelationships(SystemMap systemMap, StringBuilder plantUml, Map<SystemComponent, String> componentIds) {
        plantUml.append("' Définition des relations entre composants\n");

        for (ComponentRelationship relationship : systemMap.getRelationships()) {
            String sourceId = componentIds.get(relationship.getSource());
            String targetId = componentIds.get(relationship.getTarget());

            if (sourceId != null && targetId != null) {
                plantUml.append(sourceId).append(" --> ").append(targetId);

                // Ajouter le type et/ou la description de la relation si disponible
                String label = "";
                if (relationship.getType() != null && !relationship.getType().isEmpty()) {
                    label = relationship.getType();
                }

                if (relationship.getDescription() != null && !relationship.getDescription().isEmpty()) {
                    if (!label.isEmpty()) {
                        label += "\\n";
                    }
                    label += relationship.getDescription();
                }

                if (!label.isEmpty()) {
                    plantUml.append(" : \"").append(label).append("\"");
                }

                plantUml.append("\n");
            }
        }
    }

    private void addLegend(StringBuilder plantUml) {
        plantUml.append("\nlegend right\n");
        plantUml.append("  Cartographie générée par DigiNext\n");
        plantUml.append("  Date: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE)).append("\n");
        plantUml.append("endlegend\n\n");
    }

    public String generateSystemDiagram(SystemMap systemMap) {
        StringBuilder plantUml = new StringBuilder();
        plantUml.append("@startuml\n");
        
        configureSkinParams(plantUml);
        
        Map<SystemComponent, String> componentIds = new HashMap<>();
        defineComponents(systemMap, plantUml, componentIds);
        defineRelationships(systemMap, plantUml, componentIds);
        
        addLegend(plantUml);
        
        plantUml.append("@enduml");
        return plantUml.toString();
    }
}