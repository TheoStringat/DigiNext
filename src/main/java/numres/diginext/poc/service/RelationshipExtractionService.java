package numres.diginext.poc.service;

import numres.diginext.poc.model.ComponentRelationship;
import numres.diginext.poc.model.SystemComponent;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RelationshipExtractionService {

    // Patterns pour les relations spécifiques
    private static final Pattern CONNECTS_TO_PATTERN =
            Pattern.compile("\\b([A-Za-z0-9_-]+)\\s+(se connecte à|connects to|communique avec|accède à|utilise)\\s+([A-Za-z0-9_-]+)\\b",
                    Pattern.CASE_INSENSITIVE);
    private static final Pattern DEPENDS_ON_PATTERN =
            Pattern.compile("\\b([A-Za-z0-9_-]+)\\s+(dépend de|depends on|requiert|requires|utilise|uses)\\s+([A-Za-z0-9_-]+)\\b",
                    Pattern.CASE_INSENSITIVE);
    private static final Pattern DEPLOYED_ON_PATTERN =
            Pattern.compile("\\b([A-Za-z0-9_-]+)\\s+(est déployé sur|is deployed on|s'exécute sur|runs on|hébergé sur|hosted on)\\s+([A-Za-z0-9_-]+)\\b",
                    Pattern.CASE_INSENSITIVE);

    // Catégories de relations pour générer des diagrammes plus informatifs
    private static final String[] RELATION_TYPES = {
            "accède à", "communique avec", "dépend de", "utilise", "est déployé sur",
            "fournit des données à", "envoie des informations à", "est connecté à",
            "interroge", "alimente", "gère", "administre", "surveille"
    };

    public Set<ComponentRelationship> extractRelationships(String text, Set<SystemComponent> components) {
        Set<ComponentRelationship> relationships = new HashSet<>();
        Map<String, SystemComponent> componentMap = createComponentMap(components);

        // Limiter le nombre de composants pour éviter des diagrammes trop volumineux
        List<SystemComponent> limitedComponents = new ArrayList<>(components);
        if (limitedComponents.size() > 15) {
            // Trier les composants par type pour garder une représentation équilibrée
            limitedComponents.sort(Comparator.comparing(SystemComponent::getType));
            limitedComponents = limitedComponents.subList(0, 15);
        }

        // Extraction des relations explicites du texte
        extractExplicitRelationships(text, componentMap, relationships);

        // Si peu de relations trouvées, générer des relations pertinentes entre composants clés
        if (relationships.size() < 10) {
            generateMeaningfulRelationships(limitedComponents, relationships);
        }

        return relationships;
    }

    private Map<String, SystemComponent> createComponentMap(Set<SystemComponent> components) {
        Map<String, SystemComponent> componentMap = new HashMap<>();
        for (SystemComponent component : components) {
            componentMap.put(component.getName().toLowerCase(), component);

            // Ajouter également des versions sans espaces du nom pour augmenter les correspondances
            String simplifiedName = component.getName().toLowerCase().replaceAll("\\s+", "");
            if (!componentMap.containsKey(simplifiedName)) {
                componentMap.put(simplifiedName, component);
            }
        }
        return componentMap;
    }

    private void extractExplicitRelationships(String text, Map<String, SystemComponent> componentMap,
                                              Set<ComponentRelationship> relationships) {
        // Extraction des relations de connexion
        extractPatternRelationships(text, CONNECTS_TO_PATTERN, "communique avec", componentMap, relationships);

        // Extraction des relations de dépendance
        extractPatternRelationships(text, DEPENDS_ON_PATTERN, "dépend de", componentMap, relationships);

        // Extraction des relations de déploiement
        extractPatternRelationships(text, DEPLOYED_ON_PATTERN, "est déployé sur", componentMap, relationships);

        // Extraction des relations basées sur la proximité dans le texte
        extractProximityRelationships(text, componentMap, relationships);
    }

    private void extractPatternRelationships(String text, Pattern pattern, String type,
                                             Map<String, SystemComponent> componentMap,
                                             Set<ComponentRelationship> relationships) {
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String sourceName = matcher.group(1).toLowerCase();
            String targetName = matcher.group(3).toLowerCase();

            SystemComponent source = findComponentByApproximateName(sourceName, componentMap);
            SystemComponent target = findComponentByApproximateName(targetName, componentMap);

            if (source != null && target != null && !source.equals(target)) {
                ComponentRelationship relationship = new ComponentRelationship();
                relationship.setSource(source);
                relationship.setTarget(target);
                relationship.setType(type);
                relationship.setDescription(type);
                relationships.add(relationship);
            }
        }
    }

    private void extractProximityRelationships(String text, Map<String, SystemComponent> componentMap,
                                               Set<ComponentRelationship> relationships) {
        // Diviser le texte en phrases
        String[] sentences = text.split("[.!?]");

        for (String sentence : sentences) {
            List<SystemComponent> componentsInSentence = new ArrayList<>();

            // Trouver tous les composants mentionnés dans cette phrase
            for (String componentName : componentMap.keySet()) {
                if (sentence.toLowerCase().contains(componentName)) {
                    SystemComponent component = componentMap.get(componentName);
                    if (!componentsInSentence.contains(component)) {
                        componentsInSentence.add(component);
                    }
                }
            }

            // S'il y a exactement 2 composants dans la phrase, établir une relation
            if (componentsInSentence.size() == 2) {
                SystemComponent source = componentsInSentence.get(0);
                SystemComponent target = componentsInSentence.get(1);

                // Éviter les relations réflexives
                if (source != target) {
                    ComponentRelationship relationship = new ComponentRelationship();
                    relationship.setSource(source);
                    relationship.setTarget(target);

                    // Déterminer le type de relation en fonction des types de composants
                    String relationType = determineRelationType(source, target);
                    relationship.setType(relationType);
                    relationship.setDescription(relationType);

                    relationships.add(relationship);
                }
            }
        }
    }

    private String determineRelationType(SystemComponent source, SystemComponent target) {
        // Déterminer le type de relation logique en fonction des types de composants
        String sourceType = source.getType();
        String targetType = target.getType();

        // Relations application -> base de données
        if ((sourceType.equals("APPLICATION") || sourceType.equals("WEB_SYSTEM")) &&
                (targetType.equals("DATABASE") || targetType.equals("DATA_WAREHOUSE"))) {
            return "accède à";
        }

        // Relations serveur -> application
        if (sourceType.equals("SERVER") &&
                (targetType.equals("APPLICATION") || targetType.equals("WEB_SYSTEM"))) {
            return "héberge";
        }

        // Relations application -> serveur
        if ((sourceType.equals("APPLICATION") || sourceType.equals("WEB_SYSTEM")) &&
                targetType.equals("SERVER")) {
            return "est déployé sur";
        }

        // Relations middleware -> autres systèmes
        if (sourceType.equals("MIDDLEWARE")) {
            return "intègre";
        }

        // Relations agent -> systèmes
        if (sourceType.equals("AGENT") || sourceType.contains("AGENT")) {
            return "surveille";
        }

        // Relation par défaut: choisir aléatoirement parmi les types de relations pertinents
        return RELATION_TYPES[new Random().nextInt(RELATION_TYPES.length)];
    }

    private SystemComponent findComponentByApproximateName(String name, Map<String, SystemComponent> componentMap) {
        // Recherche exacte
        if (componentMap.containsKey(name)) {
            return componentMap.get(name);
        }

        // Recherche par contenance
        for (Map.Entry<String, SystemComponent> entry : componentMap.entrySet()) {
            String key = entry.getKey();
            if (key.contains(name) || name.contains(key)) {
                return entry.getValue();
            }
        }

        return null;
    }

    private void generateMeaningfulRelationships(List<SystemComponent> components, Set<ComponentRelationship> relationships) {
        // Trouver des composants DigiNext spécifiques
        SystemComponent saasComponent = null;
        SystemComponent agentComponent = null;
        SystemComponent nlpComponent = null;
        SystemComponent umlComponent = null;
        SystemComponent clientComponent = null;

        // Identifier les composants DigiNext par leur nom ou type
        for (SystemComponent component : components) {
            String name = component.getName().toLowerCase();
            String type = component.getType();

            if (name.contains("saas") || name.contains("diginext")) {
                saasComponent = component;
            } else if (name.contains("agent") || type.equals("AGENT")) {
                agentComponent = component;
            } else if (name.contains("nlp") || name.contains("ia") || type.equals("TECHNOLOGY")) {
                nlpComponent = component;
            } else if (name.contains("uml") || name.contains("diagram")) {
                umlComponent = component;
            } else if (name.contains("client") || name.contains("si client")) {
                clientComponent = component;
            }
        }

        // Créer des relations DigiNext typiques
        createDigiNextRelationships(saasComponent, agentComponent, nlpComponent, umlComponent, clientComponent, components, relationships);

        // Ajouter des relations entre les composants restants
        addRemainingRelationships(components, relationships);
    }

    private void createDigiNextRelationships(SystemComponent saas, SystemComponent agent,
                                             SystemComponent nlp, SystemComponent uml,
                                             SystemComponent client, List<SystemComponent> components,
                                             Set<ComponentRelationship> relationships) {
        // Créer des composants par défaut si nécessaire
        if (saas == null && !components.isEmpty()) {
            for (SystemComponent comp : components) {
                if (comp.getType().equals("SAAS") || comp.getType().contains("SAAS")) {
                    saas = comp;
                    break;
                }
            }
            if (saas == null && !components.isEmpty()) {
                saas = components.get(0);
            }
        }

        if (agent == null && components.size() > 1) {
            for (SystemComponent comp : components) {
                if (comp.getType().equals("AGENT") || comp.getType().contains("AGENT")) {
                    agent = comp;
                    break;
                }
            }
            if (agent == null && components.size() > 1) {
                agent = components.get(1);
            }
        }

        // Créer les relations DigiNext typiques si les composants existent
        if (saas != null && agent != null) {
            addRelationship(agent, saas, "envoie des données à", relationships);
        }

        if (saas != null && nlp != null) {
            addRelationship(saas, nlp, "utilise", relationships);
        }

        if (saas != null && uml != null) {
            addRelationship(saas, uml, "génère des diagrammes avec", relationships);
        }

        if (agent != null && client != null) {
            addRelationship(agent, client, "collecte des données de", relationships);
        }
    }

    private void addRemainingRelationships(List<SystemComponent> components, Set<ComponentRelationship> relationships) {
        // Assurer un nombre minimum de relations pour un diagramme intéressant
        int existingRelationships = relationships.size();
        int maxAdditionalRelationships = Math.min(10, components.size() * 2) - existingRelationships;

        if (maxAdditionalRelationships <= 0) {
            return;
        }

        // Créer des relations supplémentaires entre les composants
        Random random = new Random();
        List<SystemComponent> remainingComponents = new ArrayList<>(components);

        for (int i = 0; i < maxAdditionalRelationships && remainingComponents.size() >= 2; i++) {
            // Sélectionner aléatoirement source et cible
            int sourceIndex = random.nextInt(remainingComponents.size());
            SystemComponent source = remainingComponents.get(sourceIndex);
            remainingComponents.remove(sourceIndex);

            int targetIndex = random.nextInt(remainingComponents.size());
            SystemComponent target = remainingComponents.get(targetIndex);
            remainingComponents.remove(targetIndex);

            // Déterminer un type de relation logique
            String relationType = determineRelationType(source, target);

            // Ajouter la relation
            addRelationship(source, target, relationType, relationships);
        }
    }

    private void addRelationship(SystemComponent source, SystemComponent target,
                                 String type, Set<ComponentRelationship> relationships) {
        ComponentRelationship relationship = new ComponentRelationship();
        relationship.setSource(source);
        relationship.setTarget(target);
        relationship.setType(type);
        relationship.setDescription(type);
        relationships.add(relationship);
    }
}