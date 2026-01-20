# Share My Book

Share My Book est une application Android native développée en Kotlin, permettant la gestion d'une bibliothèque personnelle via la numérisation de codes-barres ISBN.

## Documentation et Architecture

Conformément aux consignes du projet, l'explication détaillée de l'architecture, des choix techniques (MVVM, Librairies) et de la structure de l'application est disponible dans le document suivant :

* [Rapport Technique et Architecture](docs/RAPPORT_TECHNIQUE.md)

## Installation

### Via Android Studio

1. Cloner le dépôt :
   `git clone https://github.com/lbouet61/AndroidProject_BastienLucasZakaria.git`
2. Ouvrir le projet avec Android Studio.
3. Laisser Gradle synchroniser les dépendances.
4. Lancer l'application sur un appareil physique (recommandé pour la caméra) ou un émulateur.

### Via apk

1. Récupérer 'ShareMyBookBLZ.apk'
2. Ouvrir le fichier
3. Lancer l'installation, accepter l'installation de source inconnues si nécessaire
4. Lancer l'appli une fois installée

## Problèmes connus

Sur certains appareils, lors du scans, l'appli va rester bloquée dans une boucle, il faut quitter et relancer l'application, vous trouverez vos livres scannés à l'accueuil.
Ce bug apparait sur un de nos telephones de test.

## Auteurs

* Bastien DUFOUR
* Lucas BOUET
* Zakaria BOUGARN
