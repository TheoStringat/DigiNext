package numres.diginext.poc.service;

import lombok.RequiredArgsConstructor;
import numres.diginext.poc.model.SystemMap;
import numres.diginext.poc.model.SystemComponent;
import numres.diginext.poc.model.ComponentRelationship;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentAnalysisService {

    private final ComponentExtractionService componentExtractionService;
    private final RelationshipExtractionService relationshipExtractionService;
    private final DiagramGenerationService diagramGenerationService;

    public SystemMap analyzeDocument(MultipartFile document, String documentName) throws IOException {
        String text = extractTextFromDocument(document);

        System.out.println("Texte extrait :\n" + text);

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

    /**
     * Extrait le texte brut du document fourni.
     */
    private String extractTextFromDocument(MultipartFile document) throws IOException {
        String fileName = document.getOriginalFilename();
        if (fileName != null) {
            if (fileName.toLowerCase().endsWith(".pdf")) {
                return extractTextFromPdf(document);
            } else if (fileName.toLowerCase().endsWith(".docx")) {
                return extractTextFromDocx(document);
            }
        }
        return new String(document.getBytes(), StandardCharsets.UTF_8);
    }

    /**
     * Extraction de texte depuis un PDF avec Apache PDFBox.
     */
    private String extractTextFromPdf(MultipartFile document) throws IOException {
        try (InputStream inputStream = document.getInputStream();
             PDDocument pdfDocument = PDDocument.load(inputStream)) {

            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(pdfDocument);
        }
    }

    /**
     * Extraction de texte depuis un DOCX avec Apache POI.
     */
    private String extractTextFromDocx(MultipartFile document) throws IOException {
        try (InputStream inputStream = document.getInputStream();
             XWPFDocument docxDocument = new XWPFDocument(inputStream)) {

            List<XWPFParagraph> paragraphs = docxDocument.getParagraphs();
            return paragraphs.stream()
                    .map(XWPFParagraph::getText)
                    .collect(Collectors.joining("\n"));
        }
    }
}