package numres.diginext.poc.service;

import org.springframework.stereotype.Service;
import numres.diginext.poc.model.SystemComponent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ComponentExtractionService {

    // Patterns pour détecter différents types de composants
    private static final Pattern SERVER_PATTERN =
            Pattern.compile("\\b(serveur|server)\\s+([A-Za-z0-9_-]{2,})\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern DATABASE_PATTERN =
            Pattern.compile("\\b(base de données|database|bdd|sql|oracle|mysql|postgresql)\\s+([A-Za-z0-9_-]{2,})\\b",
                    Pattern.CASE_INSENSITIVE);
    private static final Pattern APPLICATION_PATTERN =
            Pattern.compile("\\b(application|app|logiciel)\\s+([A-Za-z0-9_-]{2,})\\b", Pattern.CASE_INSENSITIVE);

    // Patterns spécifiques pour DigiNext
    private static final Pattern DIGINEXT_PATTERN =
            Pattern.compile("\\b(DigiNext|SaaS|agent local|scanner réseau|composante SaaS|interface utilisateur)\\b",
                    Pattern.CASE_INSENSITIVE);
    private static final Pattern NLP_PATTERN =
            Pattern.compile("\\b(NLP|Natural Language Processing|IA|intelligence artificielle)\\b",
                    Pattern.CASE_INSENSITIVE);
    private static final Pattern TOGAF_PATTERN =
            Pattern.compile("\\b(TOGAF|architecture|cartographie)\\b",
                    Pattern.CASE_INSENSITIVE);

    public Set<SystemComponent> extractComponents(String text) {
        Set<SystemComponent> components = new HashSet<>();
        Map<String, SystemComponent> componentMap = new HashMap<>();

        // Extraction des serveurs
        extractComponentsByPattern(text, SERVER_PATTERN, "SERVER", "Serveur détecté dans le document", componentMap);

        // Extraction des bases de données
        extractComponentsByPattern(text, DATABASE_PATTERN, "DATABASE", "Base de données détectée dans le document", componentMap);

        // Extraction des applications
        extractComponentsByPattern(text, APPLICATION_PATTERN, "APPLICATION", "Application détectée dans le document", componentMap);

        // Extraction des composants DigiNext
        extractSpecificComponents(text, componentMap);

        // Si aucun composant n'est trouvé, ajouter des composants par défaut pour DigiNext
        if (componentMap.isEmpty()) {
            addDefaultDigiNextComponents(componentMap);
        }

        components.addAll(componentMap.values());
        return components;
    }

    private void extractComponentsByPattern(String text, Pattern pattern, String type, String description,
                                            Map<String, SystemComponent> componentMap) {
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String name = matcher.group(2);
            // Vérifier que le nom n'est pas un mot court ou une préposition
            if (name.length() > 2 && !isCommonWord(name)) {
                String key = (type + "_" + name).toLowerCase();
                if (!componentMap.containsKey(key)) {
                    SystemComponent component = new SystemComponent();
                    component.setName(name);
                    component.setType(type);
                    component.setDescription(description);
                    componentMap.put(key, component);
                }
            }
        }
    }

    private void extractSpecificComponents(String text, Map<String, SystemComponent> componentMap) {
        // Extraction des composants DigiNext
        extractSpecificPattern(text, DIGINEXT_PATTERN, "DIGINEXT", "Composant DigiNext identifié dans le document", componentMap);

        // Extraction des composants NLP/IA
        extractSpecificPattern(text, NLP_PATTERN, "TECHNOLOGY", "Technologie d'IA identifiée dans le document", componentMap);

        // Extraction des composants TOGAF
        extractSpecificPattern(text, TOGAF_PATTERN, "ARCHITECTURE", "Élément d'architecture identifié dans le document", componentMap);
    }

    private void extractSpecificPattern(String text, Pattern pattern, String type, String description,
                                        Map<String, SystemComponent> componentMap) {
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String name = matcher.group(1);
            String key = (type + "_" + name).toLowerCase();
            if (!componentMap.containsKey(key)) {
                SystemComponent component = new SystemComponent();
                component.setName(name);
                component.setType(type);
                component.setDescription(description);
                componentMap.put(key, component);
            }
        }
    }

    private void addDefaultDigiNextComponents(Map<String, SystemComponent> componentMap) {
        // Composant SaaS DigiNext
        SystemComponent saasComponent = new SystemComponent();
        saasComponent.setName("DigiNext SaaS");
        saasComponent.setType("SAAS");
        saasComponent.setDescription("Composante SaaS de DigiNext pour l'interface utilisateur et l'analyse");
        componentMap.put("saas_diginext", saasComponent);

        // Agent local
        SystemComponent agentComponent = new SystemComponent();
        agentComponent.setName("Agent Local");
        agentComponent.setType("AGENT");
        agentComponent.setDescription("Agent local déployé derrière le firewall de l'entreprise");
        componentMap.put("agent_local", agentComponent);

        // Moteur NLP
        SystemComponent nlpComponent = new SystemComponent();
        nlpComponent.setName("Moteur NLP");
        nlpComponent.setType("TECHNOLOGY");
        nlpComponent.setDescription("Moteur d'analyse NLP pour l'extraction d'informations");
        componentMap.put("nlp_engine", nlpComponent);

        // Générateur UML
        SystemComponent umlComponent = new SystemComponent();
        umlComponent.setName("Générateur UML");
        umlComponent.setType("TECHNOLOGY");
        umlComponent.setDescription("Générateur de diagrammes UML pour la visualisation");
        componentMap.put("uml_generator", umlComponent);

        // SI Client
        SystemComponent clientComponent = new SystemComponent();
        clientComponent.setName("SI Client");
        clientComponent.setType("SYSTEM");
        clientComponent.setDescription("Système d'information du client à analyser");
        componentMap.put("client_si", clientComponent);
    }

    private boolean isCommonWord(String word) {
        // Liste de mots communs français à ignorer
        String[] commonWords = {"de", "des", "et", "le", "la", "les", "un", "une", "du", "au", "aux",
                "ce", "ces", "cette", "mon", "ton", "son", "nos", "vos", "leurs", "si"};

        for (String commonWord : commonWords) {
            if (word.equalsIgnoreCase(commonWord)) {
                return true;
            }
        }
        return false;
    }
}