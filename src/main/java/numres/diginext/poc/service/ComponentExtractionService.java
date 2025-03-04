package numres.diginext.poc.service;

import org.springframework.stereotype.Service;
import numres.diginext.poc.model.SystemComponent;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ComponentExtractionService {

    // Patterns pour détecter différents types de composants
    private static final Pattern SERVER_PATTERN =
            Pattern.compile("\\b(serveur|server)\\s+([A-Za-z0-9_-]+)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern DATABASE_PATTERN =
            Pattern.compile("\\b(base de données|database|bdd|sql|oracle|mysql|postgresql)\\s+([A-Za-z0-9_-]+)\\b",
                    Pattern.CASE_INSENSITIVE);
    private static final Pattern APPLICATION_PATTERN =
            Pattern.compile("\\b(application|app|logiciel)\\s+([A-Za-z0-9_-]+)\\b", Pattern.CASE_INSENSITIVE);

    public Set<SystemComponent> extractComponents(String text) {
        Set<SystemComponent> components = new HashSet<>();

        // Extraction des serveurs
        Matcher serverMatcher = SERVER_PATTERN.matcher(text);
        while (serverMatcher.find()) {
            SystemComponent component = new SystemComponent();
            component.setName(serverMatcher.group(2));
            component.setType("SERVER");
            component.setDescription("Serveur détecté dans le document");
            components.add(component);
        }

        // Extraction des bases de données
        Matcher dbMatcher = DATABASE_PATTERN.matcher(text);
        while (dbMatcher.find()) {
            SystemComponent component = new SystemComponent();
            component.setName(dbMatcher.group(2));
            component.setType("DATABASE");
            component.setDescription("Base de données détectée dans le document");
            components.add(component);
        }

        // Extraction des applications
        Matcher appMatcher = APPLICATION_PATTERN.matcher(text);
        while (appMatcher.find()) {
            SystemComponent component = new SystemComponent();
            component.setName(appMatcher.group(2));
            component.setType("APPLICATION");
            component.setDescription("Application détectée dans le document");
            components.add(component);
        }

        return components;
    }
}
