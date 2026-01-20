# Rapport Technique - Share My Book

## 1. Présentation du projet

L'objectif de l'application est de permettre à un utilisateur de scanner les codes-barres de ses livres pour constituer une bibliothèque numérique stockée localement sur son téléphone. L'application interroge une API publique pour récupérer les métadonnées (titre, auteur) à partir de l'ISBN scanné.

## 2. Architecture de l'application

Nous avons opté pour une architecture **MVVM (Model-View-ViewModel)**. Ce choix a été motivé par la nécessité de séparer clairement l'interface utilisateur de la logique métier et des données, facilitant ainsi la maintenance et les tests.

### Structure globale
L'application respecte la séparation des responsabilités suivante :

* **Model (Data Layer) :** Gère les données de l'application.
    * `Room` pour la base de données locale (cache et persistance).
    * `Retrofit` pour les appels réseaux vers l'API OpenLibrary.
    * `Repository` : Sert de point d'entrée unique pour les données, décidant de la source à utiliser (locale ou distante).
* **ViewModel :** Fait le lien entre le Modèle et la Vue. Il expose des flux de données (State) que l'interface observe. Il survit aux changements de configuration (rotation d'écran).
* **View (UI Layer) :** Développée entièrement avec **Jetpack Compose**. Elle ne contient aucune logique métier, elle se contente d'afficher l'état fourni par le ViewModel.

### Arborescence des fichiers
Le code source est organisé par couche technique :
* `data/` : Entités (Book), DAO, Définition de la BDD, Repository.
* `network/` : Service API et Client HTTP.
* `ui/` :
    * `screens/` : Les écrans composables (MenuPrincipal, Analyse, etc.).
    * `viewmodel/` : Les ViewModels associés aux écrans.

## 3. Choix Techniques et Librairies

Pour répondre aux exigences du développement Android moderne, nous avons sélectionné les technologies suivantes :

### Interface Utilisateur : Jetpack Compose
Nous avons utilisé Compose (Material 3) au lieu des fichiers XML traditionnels. Ce choix permet une écriture déclarative de l'interface, réduisant le code "boilerplate" et facilitant la gestion des états dynamiques de l'application.

### Base de données : Room
Room a été choisi pour la persistance des données locales. C'est une couche d'abstraction au-dessus de SQLite qui permet :
* Une vérification des requêtes SQL à la compilation.
* Une intégration native avec les Coroutines Kotlin.

### Réseau : Retrofit & Gson
Retrofit est utilisé pour consommer l'API REST de *openlibrary.org*. Gson assure la désérialisation automatique des réponses JSON en objets Kotlin.

### Caméra : CameraX & ML Kit
* **CameraX** : Choisi pour sa simplicité d'intégration par rapport à l'API Camera2 classique et sa compatibilité étendue avec les différents appareils Android.
* **ML Kit (Google)** : Utilisé pour la reconnaissance d'images. Le module "Barcode Scanning" nous permet d'extraire l'ISBN du flux vidéo en temps réel de manière performante.

## 4. Fonctionnalités implémentées

* **Scan :** Analyse en temps réel du flux caméra et détection automatique de format ISBN-13.
* **API :** Interrogation asynchrone pour récupérer les détails du livre.
* **Stockage :** Sauvegarde automatique en base locale après validation.
* **Liste :** Affichage réactif de la liste des livres enregistrés.

## 5. Pistes d'amélioration

Dans une version future, nous pourrions envisager :
* L'ajout d'une image de couverture pour chaque livre.
* Un système de prêt avec gestion des contacts.
* L'utilisation de Hilt pour l'injection de dépendances (actuellement gérée manuellement).