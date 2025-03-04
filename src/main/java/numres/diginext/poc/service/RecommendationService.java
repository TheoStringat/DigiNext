package numres.diginext.poc.service;

import lombok.RequiredArgsConstructor;
import numres.diginext.poc.model.SystemMap;
import numres.diginext.poc.model.SystemComponent;
import numres.diginext.poc.model.ComponentRelationship;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    /**
     * Génère des recommandations basées sur l'analyse de la cartographie du système
     *
     * @param systemMap La cartographie du système analysée
     * @return Une liste de recommandations
     */
    public List<Recommendation> generateRecommendations(SystemMap systemMap) {
        List<Recommendation> recommendations = new ArrayList<>();

        // Analyse des composants critiques (avec beaucoup de connexions)
        identifyCriticalComponents(systemMap, recommendations);

        // Détection des composants isolés
        identifyIsolatedComponents(systemMap, recommendations);

        // Analyse des technologies obsolètes ou à risque
        identifyRiskyTechnologies(systemMap, recommendations);

        // Recommandations pour l'optimisation de l'architecture
        generateArchitectureRecommendations(systemMap, recommendations);

        // Recommandations de sécurité
        generateSecurityRecommendations(systemMap, recommendations);

        return recommendations;
    }

    /**
     * Identifie les composants critiques (avec beaucoup de connexions)
     */
    private void identifyCriticalComponents(SystemMap systemMap, List<Recommendation> recommendations) {
        // Compter les connexions pour chaque composant
        Map<SystemComponent, Integer> connectionCounts = systemMap.getComponents().stream()
                .collect(Collectors.toMap(
                        component -> component,
                        component -> countConnections(component, systemMap.getRelationships())
                ));

        // Identifier les composants avec beaucoup de connexions (seuil arbitraire de 3)
        connectionCounts.entrySet().stream()
                .filter(entry -> entry.getValue() > 3)
                .forEach(entry -> {
                    recommendations.add(new Recommendation(
                            "Composant critique identifié",
                            "Le composant '" + entry.getKey().getName() + "' est un point critique avec " +
                                    entry.getValue() + " connexions. Envisagez une redondance ou une répartition de charge.",
                            "HIGH"
                    ));
                });
    }

    /**
     * Compte le nombre de connexions pour un composant
     */
    private int countConnections(SystemComponent component, Set<ComponentRelationship> relationships) {
        int count = 0;
        for (ComponentRelationship relationship : relationships) {
            if (relationship.getSource().equals(component) || relationship.getTarget().equals(component)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Identifie les composants isolés (sans connexions)
     */
    private void identifyIsolatedComponents(SystemMap systemMap, List<Recommendation> recommendations) {
        for (SystemComponent component : systemMap.getComponents()) {
            boolean hasConnections = false;
            for (ComponentRelationship relationship : systemMap.getRelationships()) {
                if (relationship.getSource().equals(component) || relationship.getTarget().equals(component)) {
                    hasConnections = true;
                    break;
                }
            }

            if (!hasConnections) {
                recommendations.add(new Recommendation(
                        "Composant isolé détecté",
                        "Le composant '" + component.getName() + "' n'a aucune connexion avec d'autres composants. " +
                                "Vérifiez s'il s'agit d'un composant orphelin ou si des connexions manquent dans la documentation.",
                        "MEDIUM"
                ));
            }
        }
    }

    /**
     * Identifie les technologies potentiellement obsolètes ou à risque
     */
    private void identifyRiskyTechnologies(SystemMap systemMap, List<Recommendation> recommendations) {
        // Liste de technologies potentiellement obsolètes ou à risque
        List<String> riskyTechnologies = List.of(
                "windows xp", "windows 7", "windows server 2008", "windows server 2003",
                "php 5", "java 6", "java 7", "java 8", "python 2",
                "internet explorer", "flash", "silverlight"
        );

        for (SystemComponent component : systemMap.getComponents()) {
            String technology = component.getTechnology();
            if (technology != null && !technology.isEmpty()) {
                for (String riskyTech : riskyTechnologies) {
                    if (technology.toLowerCase().contains(riskyTech)) {
                        recommendations.add(new Recommendation(
                                "Technologie obsolète détectée",
                                "Le composant '" + component.getName() + "' utilise " + technology +
                                        ", qui est potentiellement obsolète ou présente des risques de sécurité. " +
                                        "Envisagez une mise à niveau.",
                                "HIGH"
                        ));
                        break;
                    }
                }
            }
        }
    }

    /**
     * Génère des recommandations pour l'optimisation de l'architecture
     */
    private void generateArchitectureRecommendations(SystemMap systemMap, List<Recommendation> recommendations) {
        // Vérifier si le système a beaucoup de composants
        if (systemMap.getComponents().size() > 10) {
            recommendations.add(new Recommendation(
                    "Complexité architecturale",
                    "Le système comporte " + systemMap.getComponents().size() +
                            " composants, ce qui peut indiquer une complexité élevée. " +
                            "Envisagez une refactorisation pour simplifier l'architecture.",
                    "MEDIUM"
            ));
        }

        // Vérifier les dépendances circulaires
        checkCircularDependencies(systemMap, recommendations);
    }

    /**
     * Vérifie les dépendances circulaires entre composants
     */
    private void checkCircularDependencies(SystemMap systemMap, List<Recommendation> recommendations) {
        // Implémentation simplifiée pour le POC
        // Une analyse plus approfondie nécessiterait un algorithme de détection de cycle dans un graphe
        recommendations.add(new Recommendation(
                "Analyse des dépendances",
                "Vérifiez les dépendances circulaires entre composants qui pourraient compliquer " +
                        "la maintenance et les mises à jour du système.",
                "LOW"
        ));
    }

    /**
     * Génère des recommandations de sécurité
     */
    private void generateSecurityRecommendations(SystemMap systemMap, List<Recommendation> recommendations) {
        // Vérifier les composants exposés à l'extérieur
        for (SystemComponent component : systemMap.getComponents()) {
            if (component.getType() != null &&
                    (component.getType().equalsIgnoreCase("SERVER") ||
                            component.getType().equalsIgnoreCase("APPLICATION"))) {

                recommendations.add(new Recommendation(
                        "Vérification de sécurité recommandée",
                        "Assurez-vous que le composant '" + component.getName() +
                                "' dispose des mesures de sécurité appropriées, notamment des pare-feu, " +
                                "des mises à jour régulières et une surveillance des vulnérabilités.",
                        "MEDIUM"
                ));
            }
        }

        // Recommandation générale sur la sécurité des données
        recommendations.add(new Recommendation(
                "Protection des données",
                "Vérifiez que toutes les données sensibles sont chiffrées, tant au repos qu'en transit, " +
                        "et que les accès sont correctement contrôlés et audités.",
                "HIGH"
        ));
    }

    /**
     * Classe interne pour représenter une recommandation
     */
    public static class Recommendation {
        private String title;
        private String description;
        private String priority;

        public Recommendation(String title, String description, String priority) {
            this.title = title;
            this.description = description;
            this.priority = priority;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getPriority() {
            return priority;
        }
    }
}