package numres.diginext.poc.controller;

import lombok.RequiredArgsConstructor;
import numres.diginext.poc.model.SystemMap;
import numres.diginext.poc.service.DocumentAnalysisService;
import numres.diginext.poc.service.RecommendationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final DocumentAnalysisService documentAnalysisService;
    private final RecommendationService recommendationService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/analyze")
    public String analyzeDocument(@RequestParam("file") MultipartFile file,
                                  @RequestParam("name") String name,
                                  Model model) {
        try {
            SystemMap systemMap = documentAnalysisService.analyzeDocument(file, name);
            model.addAttribute("systemMap", systemMap);
            model.addAttribute("recommendations", recommendationService.generateRecommendations(systemMap));
            return "result";
        } catch (IOException e) {
            model.addAttribute("error", "Erreur lors de l'analyse du document: " + e.getMessage());
            return "index";
        }
    }

    // Ajoutez cette méthode pour gérer les requêtes GET
    @GetMapping("/analyze")
    public String showAnalyzePage() {
        return "index";
    }
}