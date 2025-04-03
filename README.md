# DigiNext - Analyse Automatisée de Systèmes d'Information

## 📋 Description

DigiNext est une application innovante d'analyse automatisée de Systèmes d'Information (SI) qui permet d'extraire des informations à partir de documents (PDF, DOCX, texte) pour construire une cartographie visuelle du SI et générer des recommandations d'amélioration.

Cette Proof of Concept (POC) démontre la capacité de DigiNext à :

- Analyser des documents décrivant un SI
- Extraire automatiquement les composants et leurs relations
- Générer une cartographie visuelle sous forme de diagramme UML
- Proposer des recommandations pertinentes pour l'optimisation du SI

## 🚀 Fonctionnalités

- Extraction de composants : Identification automatique des serveurs, applications, bases de données, et autres éléments du SI
- Détection de relations : Analyse des connexions et dépendances entre les composants
- Visualisation UML : Génération de diagrammes PlantUML pour représenter l'architecture du SI
- Recommandations intelligentes : Suggestions pour améliorer la sécurité, la performance et la résilience du SI
- Support multi-format : Analyse de documents PDF, DOCX et TXT

## 🛠️ Technologies utilisées

- Spring Boot : Framework Java pour le développement d'applications web
- Thymeleaf : Moteur de templates pour les vues HTML
- Apache PDFBox : Extraction de texte depuis des documents PDF
- Apache POI : Extraction de texte depuis des documents DOCX
- PlantUML : Génération de diagrammes UML
- Bootstrap : Framework CSS pour l'interface utilisateur
- H2 Database : Base de données en mémoire pour le développement

## 📦 Prérequis

- Java 17 ou supérieur
- Maven 3.6 ou supérieur

## 🔧 Installation et démarrage

- Clonez le dépôt :
- bash :
```
git clone https://github.com/TheoStringat/DigiNext.git
cd DigiNext
```
- Compilez et lancez l'application :
- bash :
```
mvn spring-boot:run
```
- Accédez à l'application dans votre navigateur :
http://localhost:8080/

## 📝 Utilisation

Sur la page d'accueil, entrez un nom pour votre projet d'analyse
Téléchargez un document décrivant le SI à analyser (PDF, DOCX ou TXT)
Cliquez sur "Analyser"

Consultez les résultats :
- Diagramme : Visualisation UML du SI
- Composants : Liste détaillée des composants identifiés
- Recommandations : Suggestions pour améliorer le SI

## 🧩 Architecture

L'application suit une architecture MVC classique avec les composants suivants :

- Contrôleurs : Gestion des requêtes HTTP et des interactions utilisateur
- Services : Logique métier pour l'analyse des documents et l'extraction des informations
- Modèles : Représentation des composants du SI et de leurs relations 
- Vues : Templates Thymeleaf pour l'interface utilisateur

## 🔍 Fonctionnement technique

- Analyse de document : Le document téléchargé est traité pour en extraire le texte brut
- Extraction des composants : Des expressions régulières identifient les différents types de composants du SI
- Détection des relations : Les connexions entre composants sont identifiées par analyse contextuelle
- Génération du diagramme : Un code PlantUML est généré pour visualiser l'architecture
- Recommandations : Des algorithmes d'analyse identifient les points d'amélioration potentiels

## 👥 Équipe

- Théo STRINGAT - Président-Directeur Général (PDG) et Responsable Stratégie et Technique
- Sawsen EL BAHRI - Responsable de l'Innovation et de l'Intelligence Artificielle
- Pierre SAVE - Responsable des Opérations et des Partenariats


© 2025 DigiNext - Tous droits réservés