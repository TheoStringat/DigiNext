package numres.diginext.poc.service;

import org.springframework.stereotype.Service;
import numres.diginext.poc.model.SystemMap;
import numres.diginext.poc.model.SystemComponent;
import numres.diginext.poc.model.ComponentRelationship;

@Service
public class DiagramGenerationService {

    public String generatePlantUML(SystemMap systemMap) {
        StringBuilder plantUml = new StringBuilder();
        plantUml.append("@startuml\n");
        plantUml.append("title Cartographie du Système d'Information\n\n");

        // Définition des composants
        for (SystemComponent component : systemMap.getComponents()) {
            String type = component.getType();
            String cleanName = cleanId(component.getName());

            switch (type) {
                case "DATABASE":
                    plantUml.append("database \"").append(component.getName())
                            .append("\" as ").append(cleanName).append("\n");
                    break;
                case "SERVER":
                    plantUml.append("node \"").append(component.getName())
                            .append("\" as ").append(cleanName).append("\n");
                    break;
                case "APPLICATION":
                    plantUml.append("rectangle \"").append(component.getName())
                            .append("\" as ").append(cleanName).append("\n");
                    break;
                default:
                    plantUml.append("component \"").append(component.getName())
                            .append("\" as ").append(cleanName).append("\n");
            }
        }

        plantUml.append("\n");

        // Définition des relations
        for (ComponentRelationship relationship : systemMap.getRelationships()) {
            plantUml.append(cleanId(relationship.getSource().getName()))
                    .append(" --> ")
                    .append(cleanId(relationship.getTarget().getName()));

            if (relationship.getDescription() != null && !relationship.getDescription().isEmpty()) {
                plantUml.append(" : ").append(relationship.getDescription());
            }

            plantUml.append("\n");
        }

        plantUml.append("@enduml");

        return plantUml.toString();
    }

    private String cleanId(String text) {
        // Nettoie le texte pour qu'il soit utilisable comme ID dans PlantUML
        return text.replaceAll("[^a-zA-Z0-9]", "_");
    }
}