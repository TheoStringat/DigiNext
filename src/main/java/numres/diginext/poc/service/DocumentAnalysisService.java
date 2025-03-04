package numres.diginext.poc.service;

import lombok.RequiredArgsConstructor;
import numres.diginext.poc.model.SystemMap;
import numres.diginext.poc.model.SystemComponent;
import numres.diginext.poc.model.ComponentRelationship;
import numres.diginext.poc.service.ComponentExtractionService;
import numres.diginext.poc.service.RelationshipExtractionService;
import numres.diginext.poc.service.DiagramGenerationService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DocumentAnalysisService {

    private final ComponentExtractionService componentExtractionService;
    private final RelationshipExtractionService relationshipExtractionService;
    private final DiagramGenerationService diagramGenerationService;

    public SystemMap analyzeDocument(MultipartFile document, String documentName) throws IOException {
        String text = extractTextFromDocument(document);

        // Extraction des composants du SI
        Set<SystemComponent> components = componentExtractionService.extractComponents(text);

        // Identification des relations entre composants
        Set<ComponentRelationship> relationships =
                relationshipExtractionService.extractRelationships(text, components);

        // Création de la cartographie
        SystemMap systemMap = new SystemMap();
        systemMap.setName(documentName);
        systemMap.setDescription("Cartographie générée à partir de " + document.getOriginalFilename());
        systemMap.setCreatedBy("DigiNext POC");
        systemMap.setCreatedDate(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        systemMap.setComponents(components);
        systemMap.setRelationships(relationships);

        // Génération du diagramme PlantUML
        String plantUmlDiagram = diagramGenerationService.generatePlantUML(systemMap);
        systemMap.setPlantUmlDiagram(plantUmlDiagram);

        return systemMap;
    }

    private String extractTextFromDocument(MultipartFile document) throws IOException {
        // Logique d'extraction de texte selon le type de document
        String fileName = document.getOriginalFilename();
        if (fileName != null) {
            if (fileName.endsWith(".pdf")) {
                return extractTextFromPdf(document);
            } else if (fileName.endsWith(".docx")) {
                return extractTextFromDocx(document);
            }
        }
        return new String(document.getBytes(), StandardCharsets.UTF_8);
    }

    private String extractTextFromPdf(MultipartFile document) {
        // Implémentation de l'extraction de texte à partir d'un PDF
        // Utiliser une bibliothèque comme Apache PDFBox
        return "Texte extrait du PDF"; // À implémenter
    }

    private String extractTextFromDocx(MultipartFile document) {
        // Implémentation de l'extraction de texte à partir d'un DOCX
        // Utiliser une bibliothèque comme Apache POI
        return "Texte extrait du DOCX"; // À implémenter
    }
}
