package numres.diginext.poc.service;

import org.springframework.stereotype.Service;
import numres.diginext.poc.model.ComponentRelationship;
import numres.diginext.poc.model.SystemComponent;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RelationshipExtractionService {

    // Patterns pour détecter les relations entre composants
    private static final Pattern DIRECT_RELATION_PATTERN =
            Pattern.compile("([A-Za-z0-9_-]+)\\s+(communique avec|se connecte à|utilise|dépend de|interagit avec)\\s+([A-Za-z0-9_-]+)",
                    Pattern.CASE_INSENSITIVE);

    private static final Pattern INDIRECT_RELATION_PATTERN =
            Pattern.compile("([A-Za-z0-9_-]+)\\s+et\\s+([A-Za-z0-9_-]+)\\s+(sont connectés|interagissent|communiquent)",
                    Pattern.CASE_INSENSITIVE);

    private static final Pattern DATA_FLOW_PATTERN =
            Pattern.compile("(données|informations)\\s+(circulent|transitent|passent)\\s+de\\s+([A-Za-z0-9_-]+)\\s+(vers|à)\\s+([A-Za-z0-9_-]+)",
                    Pattern.CASE_INSENSITIVE);

    /**
     * Extrait les relations entre les composants à partir du texte
     *
     * @param text Le texte à analyser
     * @param components L'ensemble des composants déjà identifiés
     * @return Un ensemble de relations entre composants
     */
    public Set<ComponentRelationship> extractRelationships(String text, Set<SystemComponent> components) {
        Set<ComponentRelationship> relationships = new HashSet<>();

        // Extraction des relations directes
        extractDirectRelationships(text, components, relationships);

        // Extraction des relations indirectes
        extractIndirectRelationships(text, components, relationships);

        // Extraction des flux de données
        extractDataFlowRelationships(text, components, relationships);

        // Inférence de relations basée sur la proximité dans le texte
        inferRelationshipsFromProximity(text, components, relationships);

        return relationships;
    }

    /**
     * Extrait les relations directes entre composants (A communique avec B)
     */
    private void extractDirectRelationships(String text, Set<SystemComponent> components,
                                            Set<ComponentRelationship> relationships) {
        Matcher matcher = DIRECT_RELATION_PATTERN.matcher(text);
        while (matcher.find()) {
            String sourceName = matcher.group(1);
            String relationType = matcher.group(2);
            String targetName = matcher.group(3);

            SystemComponent source = findComponentByName(components, sourceName);
            SystemComponent target = findComponentByName(components, targetName);

            if (source != null && target != null) {
                ComponentRelationship relationship = new ComponentRelationship();
                relationship.setSource(source);
                relationship.setTarget(target);
                relationship.setType(mapRelationType(relationType));
                relationship.setDescription(sourceName + " " + relationType + " " + targetName);
                relationships.add(relationship);
            }
        }
    }

    /**
     * Extrait les relations indirectes entre composants (A et B sont connectés)
     */
    private void extractIndirectRelationships(String text, Set<SystemComponent> components,
                                              Set<ComponentRelationship> relationships) {
        Matcher matcher = INDIRECT_RELATION_PATTERN.matcher(text);
        while (matcher.find()) {
            String comp1Name = matcher.group(1);
            String comp2Name = matcher.group(2);
            String relationType = matcher.group(3);

            SystemComponent comp1 = findComponentByName(components, comp1Name);
            SystemComponent comp2 = findComponentByName(components, comp2Name);

            if (comp1 != null && comp2 != null) {
                // Création d'une relation bidirectionnelle
                ComponentRelationship relationship1 = new ComponentRelationship();
                relationship1.setSource(comp1);
                relationship1.setTarget(comp2);
                relationship1.setType("COMMUNICATES_WITH");
                relationship1.setDescription(comp1Name + " et " + comp2Name + " " + relationType);
                relationships.add(relationship1);

                ComponentRelationship relationship2 = new ComponentRelationship();
                relationship2.setSource(comp2);
                relationship2.setTarget(comp1);
                relationship2.setType("COMMUNICATES_WITH");
                relationship2.setDescription(comp2Name + " et " + comp1Name + " " + relationType);
                relationships.add(relationship2);
            }
        }
    }

    /**
     * Extrait les relations de flux de données (données circulent de A vers B)
     */
    private void extractDataFlowRelationships(String text, Set<SystemComponent> components,
                                              Set<ComponentRelationship> relationships) {
        Matcher matcher = DATA_FLOW_PATTERN.matcher(text);
        while (matcher.find()) {
            String sourceName = matcher.group(3);
            String targetName = matcher.group(5);

            SystemComponent source = findComponentByName(components, sourceName);
            SystemComponent target = findComponentByName(components, targetName);

            if (source != null && target != null) {
                ComponentRelationship relationship = new ComponentRelationship();
                relationship.setSource(source);
                relationship.setTarget(target);
                relationship.setType("DATA_FLOW");
                relationship.setDescription("Flux de données de " + sourceName + " vers " + targetName);
                relationships.add(relationship);
            }
        }
    }

    /**
     * Infère des relations basées sur la proximité des composants dans le texte
     */
    private void inferRelationshipsFromProximity(String text, Set<SystemComponent> components,
                                                 Set<ComponentRelationship> relationships) {
        // Diviser le texte en paragraphes
        String[] paragraphs = text.split("\n\n");

        for (String paragraph : paragraphs) {
            Set<SystemComponent> componentsInParagraph = new HashSet<>();

            // Identifier les composants mentionnés dans ce paragraphe
            for (SystemComponent component : components) {
                if (paragraph.toLowerCase().contains(component.getName().toLowerCase())) {
                    componentsInParagraph.add(component);
                }
            }

            // Si au moins deux composants sont mentionnés dans le même paragraphe,
            // on peut inférer une relation potentielle entre eux
            if (componentsInParagraph.size() >= 2) {
                SystemComponent[] comps = componentsInParagraph.toArray(new SystemComponent[0]);

                for (int i = 0; i < comps.length; i++) {
                    for (int j = i + 1; j < comps.length; j++) {
                        // Vérifier si cette relation n'existe pas déjà
                        if (!relationshipExists(relationships, comps[i], comps[j])) {
                            ComponentRelationship relationship = new ComponentRelationship();
                            relationship.setSource(comps[i]);
                            relationship.setTarget(comps[j]);
                            relationship.setType("POTENTIAL_RELATION");
                            relationship.setDescription("Relation potentielle (mentionnés ensemble)");
                            relationships.add(relationship);
                        }
                    }
                }
            }
        }
    }

    /**
     * Vérifie si une relation entre deux composants existe déjà
     */
    private boolean relationshipExists(Set<ComponentRelationship> relationships,
                                       SystemComponent source, SystemComponent target) {
        for (ComponentRelationship rel : relationships) {
            if ((rel.getSource().equals(source) && rel.getTarget().equals(target)) ||
                    (rel.getSource().equals(target) && rel.getTarget().equals(source))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Trouve un composant par son nom
     */
    private SystemComponent findComponentByName(Set<SystemComponent> components, String name) {
        for (SystemComponent component : components) {
            if (component.getName().equalsIgnoreCase(name)) {
                return component;
            }
        }
        return null;
    }

    /**
     * Mappe les types de relations textuelles vers des types standardisés
     */
    private String mapRelationType(String textualRelation) {
        textualRelation = textualRelation.toLowerCase();

        if (textualRelation.contains("communique") || textualRelation.contains("connecte")) {
            return "COMMUNICATES_WITH";
        } else if (textualRelation.contains("utilise") || textualRelation.contains("dépend")) {
            return "DEPENDS_ON";
        } else if (textualRelation.contains("interagit")) {
            return "INTERACTS_WITH";
        } else {
            return "RELATED_TO";
        }
    }
}