package numres.diginext.poc.service;

import org.springframework.stereotype.Service;
import numres.diginext.poc.model.SystemComponent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ComponentExtractionService {

    // ARCHITECTURE ET INFRASTRUCTURE
    private static final Pattern SERVER_PATTERN =
            Pattern.compile("\\b(serveur|server|machine|host|nœud|node)\\s+([A-Za-z0-9_.-]{2,})\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern NETWORK_DEVICE_PATTERN =
            Pattern.compile("\\b(routeur|router|switch|firewall|pare-feu|load balancer|répartiteur de charge|proxy|passerelle|gateway)\\s+([A-Za-z0-9_.-]{2,})\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern CLOUD_PATTERN =
            Pattern.compile("\\b(cloud|AWS|Azure|GCP|Google Cloud|S3|EC2|Lambda|Azure Functions)\\s+([A-Za-z0-9_.-]{2,})?\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern VIRTUALIZATION_PATTERN =
            Pattern.compile("\\b(VM|machine virtuelle|virtual machine|conteneur|container|docker|kubernetes|k8s|pod|cluster)\\s+([A-Za-z0-9_.-]{2,})?\\b", Pattern.CASE_INSENSITIVE);

    // DONNÉES ET STOCKAGE
    private static final Pattern DATABASE_PATTERN =
            Pattern.compile("\\b(base de données|database|bdd|db|sql|oracle|mysql|postgresql|mongodb|nosql|sqlite|mariadb|cassandra|redis|elasticsearch)\\s+([A-Za-z0-9_.-]{2,})?\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern DATA_WAREHOUSE_PATTERN =
            Pattern.compile("\\b(entrepôt de données|data warehouse|data lake|lac de données|big data|hadoop|spark|dataproc|snowflake)\\s+([A-Za-z0-9_.-]{2,})?\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern FILE_STORAGE_PATTERN =
            Pattern.compile("\\b(stockage fichier|file storage|NAS|SAN|partage réseau|network share|GFS|HDFS|EFS)\\s+([A-Za-z0-9_.-]{2,})?\\b", Pattern.CASE_INSENSITIVE);

    // LOGICIELS ET APPLICATIONS
    private static final Pattern APPLICATION_PATTERN =
            Pattern.compile("\\b(application|app|logiciel|software|système|system|plateforme|platform|portail|portal)\\s+([A-Za-z0-9_.-]{2,})\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern ERP_CRM_PATTERN =
            Pattern.compile("\\b(ERP|SAP|Oracle EBS|PeopleSoft|Microsoft Dynamics|Sage|CRM|Salesforce|Microsoft Dynamics CRM|SugarCRM)\\s+([A-Za-z0-9_.-]{2,})?\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern WEB_PATTERN =
            Pattern.compile("\\b(site web|website|application web|web app|intranet|extranet|webapp|serveur web|web server|apache|nginx|IIS)\\s+([A-Za-z0-9_.-]{2,})?\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern MIDDLEWARE_PATTERN =
            Pattern.compile("\\b(middleware|API Gateway|ESB|bus de service|ETL|Talend|Informatica|MuleSoft|RabbitMQ|Kafka|ActiveMQ|JMS|message broker)\\s+([A-Za-z0-9_.-]{2,})?\\b", Pattern.CASE_INSENSITIVE);

    // SÉCURITÉ
    private static final Pattern SECURITY_PATTERN =
            Pattern.compile("\\b(pare-feu|firewall|WAF|IDS|IPS|VPN|DMZ|bastion|proxy|authentification|authentication|autorisation|authorization|IAM|Active Directory|LDAP|SSO)\\s+([A-Za-z0-9_.-]{2,})?\\b", Pattern.CASE_INSENSITIVE);

    // ÉLÉMENTS MÉTIER
    private static final Pattern BUSINESS_PROCESS_PATTERN =
            Pattern.compile("\\b(processus|process|workflow|flux de travail|business process|BPMN)\\s+([A-Za-z0-9_.-]{2,})\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern DEPARTMENT_PATTERN =
            Pattern.compile("\\b(département|department|service|direction|division)\\s+([A-Za-z0-9_.-]{2,})\\b", Pattern.CASE_INSENSITIVE);

    // Patterns spécifiques pour DigiNext
    private static final Pattern DIGINEXT_PATTERN =
            Pattern.compile("\\b(DigiNext|SaaS|agent local|scanner réseau|composante SaaS|interface utilisateur|agent|scanner)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern NLP_PATTERN =
            Pattern.compile("\\b(NLP|Natural Language Processing|IA|intelligence artificielle|AI|machine learning|apprentissage automatique|deep learning|GPT)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern TOGAF_PATTERN =
            Pattern.compile("\\b(TOGAF|architecture|cartographie|mapping|urbanisation|SOA|microservices)\\b", Pattern.CASE_INSENSITIVE);

    // Patterns pour les versions et environnements
    private static final Pattern VERSION_PATTERN =
            Pattern.compile("\\b(version|v)\\s*(\\d+(?:\\.\\d+){0,2})\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern ENVIRONMENT_PATTERN =
            Pattern.compile("\\b(environnement|environment|env)\\s*(de|:|-)?(\\s*)(production|prod|développement|dev|test|staging|qualification|recette|pré-production|preprod)\\b", Pattern.CASE_INSENSITIVE);

    public Set<SystemComponent> extractComponents(String text) {
        Set<SystemComponent> components = new HashSet<>();
        Map<String, SystemComponent> componentMap = new HashMap<>();

        // EXTRACTION DES COMPOSANTS D'INFRASTRUCTURE
        extractComponentsByPattern(text, SERVER_PATTERN, "SERVER", "Serveur physique ou virtuel", componentMap);
        extractComponentsByPattern(text, NETWORK_DEVICE_PATTERN, "NETWORK_DEVICE", "Équipement réseau", componentMap);
        extractComponentsByPattern(text, CLOUD_PATTERN, "CLOUD_SERVICE", "Service cloud", componentMap);
        extractComponentsByPattern(text, VIRTUALIZATION_PATTERN, "VIRTUALIZATION", "Environnement virtualisé", componentMap);

        // EXTRACTION DES COMPOSANTS DE STOCKAGE
        extractComponentsByPattern(text, DATABASE_PATTERN, "DATABASE", "Base de données ou système de gestion de données", componentMap);
        extractComponentsByPattern(text, DATA_WAREHOUSE_PATTERN, "DATA_WAREHOUSE", "Entrepôt ou lac de données", componentMap);
        extractComponentsByPattern(text, FILE_STORAGE_PATTERN, "FILE_STORAGE", "Stockage de fichiers", componentMap);

        // EXTRACTION DES APPLICATIONS
        extractComponentsByPattern(text, APPLICATION_PATTERN, "APPLICATION", "Application métier", componentMap);
        extractComponentsByPattern(text, ERP_CRM_PATTERN, "ENTERPRISE_SYSTEM", "Système d'entreprise (ERP, CRM, etc.)", componentMap);
        extractComponentsByPattern(text, WEB_PATTERN, "WEB_SYSTEM", "Système ou application web", componentMap);
        extractComponentsByPattern(text, MIDDLEWARE_PATTERN, "MIDDLEWARE", "Middleware ou système d'intégration", componentMap);

        // EXTRACTION DES COMPOSANTS DE SÉCURITÉ
        extractComponentsByPattern(text, SECURITY_PATTERN, "SECURITY", "Système ou dispositif de sécurité", componentMap);

        // EXTRACTION DES ÉLÉMENTS MÉTIER
        extractComponentsByPattern(text, BUSINESS_PROCESS_PATTERN, "BUSINESS_PROCESS", "Processus métier", componentMap);
        extractComponentsByPattern(text, DEPARTMENT_PATTERN, "DEPARTMENT", "Département ou unité organisationnelle", componentMap);

        // EXTRACTION DES COMPOSANTS SPÉCIFIQUES À DIGINEXT
        extractSpecificComponents(text, componentMap);

        // ENRICHISSEMENT AVEC DES MÉTADONNÉES
        enrichComponentsWithMetadata(text, componentMap);

        // Si aucun composant n'est trouvé, ajouter des composants par défaut pour DigiNext
        if (componentMap.isEmpty()) {
            addDefaultDigiNextComponents(componentMap);
        }

        components.addAll(componentMap.values());
        return components;
    }

    private void extractComponentsByPattern(String text, Pattern pattern, String type, String baseDescription,
                                            Map<String, SystemComponent> componentMap) {
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String name;
            // Certains patterns n'ont pas forcément un deuxième groupe (ex: "cloud AWS" sans nom spécifique)
            if (matcher.groupCount() >= 2 && matcher.group(2) != null && !matcher.group(2).isEmpty()) {
                name = matcher.group(1) + " " + matcher.group(2);
            } else {
                name = matcher.group(1);
            }

            // Vérifier que le nom n'est pas un mot court ou une préposition
            if (name.length() > 2 && !isCommonWord(name)) {
                String key = (type + "_" + name).toLowerCase().replaceAll("\\s+", "_");
                if (!componentMap.containsKey(key)) {
                    SystemComponent component = new SystemComponent();
                    component.setName(name);
                    component.setType(type);
                    component.setDescription(baseDescription + " identifié dans le document");

                    // Recherche de contexte supplémentaire autour du composant
                    String context = extractContext(text, matcher.start(), matcher.end());
                    if (!context.isEmpty()) {
                        component.setDescription(component.getDescription() + ". Contexte: " + context);
                    }

                    componentMap.put(key, component);
                }
            }
        }
    }

    private void extractSpecificComponents(String text, Map<String, SystemComponent> componentMap) {
        // Extraction des composants DigiNext
        extractSpecificPattern(text, DIGINEXT_PATTERN, "DIGINEXT", "Composant DigiNext pour la cartographie des SI", componentMap);

        // Extraction des technologies IA/NLP
        extractSpecificPattern(text, NLP_PATTERN, "TECHNOLOGY", "Technologie d'intelligence artificielle/NLP", componentMap);

        // Extraction des éléments d'architecture
        extractSpecificPattern(text, TOGAF_PATTERN, "ARCHITECTURE", "Concept d'architecture d'entreprise", componentMap);
    }

    private void extractSpecificPattern(String text, Pattern pattern, String type, String description,
                                        Map<String, SystemComponent> componentMap) {
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String name = matcher.group(1);
            String key = (type + "_" + name).toLowerCase().replaceAll("\\s+", "_");
            if (!componentMap.containsKey(key)) {
                SystemComponent component = new SystemComponent();
                component.setName(name);
                component.setType(type);
                component.setDescription(description);

                // Recherche de contexte supplémentaire autour du composant
                String context = extractContext(text, matcher.start(), matcher.end());
                if (!context.isEmpty()) {
                    component.setDescription(component.getDescription() + ". Contexte: " + context);
                }

                componentMap.put(key, component);
            }
        }
    }

    private void enrichComponentsWithMetadata(String text, Map<String, SystemComponent> componentMap) {
        // Pour chaque composant, chercher des informations sur la version et l'environnement
        for (SystemComponent component : componentMap.values()) {
            String componentName = component.getName().toLowerCase();

            // Recherche de versions associées au composant
            Pattern versionForComponentPattern =
                    Pattern.compile("\\b" + Pattern.quote(componentName) + "\\b[^.]{1,30}?" + VERSION_PATTERN.pattern(),
                            Pattern.CASE_INSENSITIVE);

            Matcher versionMatcher = versionForComponentPattern.matcher(text);
            if (versionMatcher.find()) {
                String version = versionMatcher.group(versionMatcher.groupCount());
                component.setDescription(component.getDescription() + " (Version " + version + ")");
                // On pourrait ajouter un attribut "version" au modèle SystemComponent
            }

            // Recherche d'environnements associés au composant
            Pattern envForComponentPattern =
                    Pattern.compile("\\b" + Pattern.quote(componentName) + "\\b[^.]{1,30}?" + ENVIRONMENT_PATTERN.pattern(),
                            Pattern.CASE_INSENSITIVE);

            Matcher envMatcher = envForComponentPattern.matcher(text);
            if (envMatcher.find()) {
                String environment = envMatcher.group(envMatcher.groupCount());
                component.setDescription(component.getDescription() + " (Environnement: " + environment + ")");
                // On pourrait ajouter un attribut "environment" au modèle SystemComponent
            }

            // Détection de criticité/importance
            if (text.toLowerCase().contains("critique") && text.toLowerCase().contains(componentName) ||
                    text.toLowerCase().contains("critical") && text.toLowerCase().contains(componentName) ||
                    text.toLowerCase().contains("important") && text.toLowerCase().contains(componentName) ||
                    text.toLowerCase().contains("prioritaire") && text.toLowerCase().contains(componentName)) {

                component.setDescription(component.getDescription() + " [CRITIQUE]");
                // On pourrait ajouter un attribut "criticalLevel" au modèle SystemComponent
            }
        }
    }

    private String extractContext(String text, int startPos, int endPos) {
        // Extraire une fenêtre de texte avant et après la mention du composant (max 50 caractères de chaque côté)
        int contextStart = Math.max(0, startPos - 50);
        int contextEnd = Math.min(text.length(), endPos + 50);

        String context = text.substring(contextStart, contextEnd).trim();

        // Nettoyer le contexte pour qu'il soit lisible
        context = context.replaceAll("\\s+", " ");

        return context;
    }

    private void addDefaultDigiNextComponents(Map<String, SystemComponent> componentMap) {
        // Architecture globale DigiNext (plus détaillée qu'avant)

        // Composant SaaS DigiNext
        SystemComponent saasComponent = new SystemComponent();
        saasComponent.setName("DigiNext SaaS");
        saasComponent.setType("SAAS");
        saasComponent.setDescription("Composante SaaS de DigiNext pour l'interface utilisateur et l'analyse des données collectées");
        componentMap.put("saas_diginext", saasComponent);

        // Agent local
        SystemComponent agentComponent = new SystemComponent();
        agentComponent.setName("Agent Local DigiNext");
        agentComponent.setType("AGENT");
        agentComponent.setDescription("Agent local déployé derrière le firewall de l'entreprise pour collecter les données du SI en toute sécurité");
        componentMap.put("agent_local", agentComponent);

        // Moteur NLP
        SystemComponent nlpComponent = new SystemComponent();
        nlpComponent.setName("Moteur NLP");
        nlpComponent.setType("TECHNOLOGY");
        nlpComponent.setDescription("Moteur d'analyse NLP pour l'extraction d'informations à partir des documents et métadonnées du SI");
        componentMap.put("nlp_engine", nlpComponent);

        // Générateur UML
        SystemComponent umlComponent = new SystemComponent();
        umlComponent.setName("Générateur UML");
        umlComponent.setType("TECHNOLOGY");
        umlComponent.setDescription("Générateur de diagrammes UML pour la visualisation de l'architecture du SI");
        componentMap.put("uml_generator", umlComponent);

        // SI Client (exemple)
        SystemComponent clientComponent = new SystemComponent();
        clientComponent.setName("SI Client");
        clientComponent.setType("SYSTEM");
        clientComponent.setDescription("Système d'information client à analyser");
        componentMap.put("client_si", clientComponent);

        // Base de données client (exemple)
        SystemComponent clientDbComponent = new SystemComponent();
        clientDbComponent.setName("Base de données centrale");
        clientDbComponent.setType("DATABASE");
        clientDbComponent.setDescription("Base de données principale du SI client stockant les données métier");
        componentMap.put("client_database", clientDbComponent);

        // Application métier (exemple)
        SystemComponent businessAppComponent = new SystemComponent();
        businessAppComponent.setName("Application métier");
        businessAppComponent.setType("APPLICATION");
        businessAppComponent.setDescription("Application principale supportant les processus métier de l'entreprise");
        componentMap.put("business_app", businessAppComponent);

        // Serveur d'applications (exemple)
        SystemComponent appServerComponent = new SystemComponent();
        appServerComponent.setName("Serveur d'applications");
        appServerComponent.setType("SERVER");
        appServerComponent.setDescription("Serveur hébergeant les applications métier du SI client");
        componentMap.put("app_server", appServerComponent);

        // Interface utilisateur DigiNext
        SystemComponent uiComponent = new SystemComponent();
        uiComponent.setName("Interface Utilisateur DigiNext");
        uiComponent.setType("WEB_SYSTEM");
        uiComponent.setDescription("Interface web permettant de visualiser les cartographies et recommandations produites par DigiNext");
        componentMap.put("diginext_ui", uiComponent);

        // Système de recommandations
        SystemComponent recommenderComponent = new SystemComponent();
        recommenderComponent.setName("Moteur de recommandations");
        recommenderComponent.setType("TECHNOLOGY");
        recommenderComponent.setDescription("Système d'IA générant des recommandations d'optimisation du SI basées sur l'analyse");
        componentMap.put("recommender_engine", recommenderComponent);
    }

    private boolean isCommonWord(String word) {
        // Liste de mots communs français à ignorer
        String[] commonWords = {"de", "des", "et", "le", "la", "les", "un", "une", "du", "au", "aux",
                "ce", "ces", "cette", "mon", "ton", "son", "nos", "vos", "leurs", "si", "pour", "par",
                "avec", "sans", "dans", "sur", "sous", "vers", "comme", "mais", "ou", "où", "qui", "que",
                "quoi", "dont", "comment", "exemple"};

        for (String commonWord : commonWords) {
            if (word.equalsIgnoreCase(commonWord)) {
                return true;
            }
        }
        return false;
    }
}