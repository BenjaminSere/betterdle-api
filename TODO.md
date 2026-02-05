# TODO

Gestion du stockage des images des champions.
Définire comment on va géré les utilisateur sur une autre DB ? Ou on peux faire comme dans Django crée des apps ?

# TODO Liste : Finalisation de l'Initialisation et Gestion des Données

---

## 1. Gestion de la Version et Persistance

**Objectif** : Éviter le rechargement inutile des données si la version n'a pas changé.

- [ ] **Créer une entité `GameVersion`**
  - Table en base de données pour stocker :
    - Nom du jeu (ex: `lol`)
    - Dernière version initialisée (ex: `14.1.1`)

- [ ] **Créer `GameVersionRepository`**
  - Méthodes pour :
    - Lire la version actuelle
    - Mettre à jour la version

- [ ] **Logique de contrôle dans `Initializator`**
  - Récupérer la version distante via l'API Riot
  - Comparer avec la version stockée en BDD
  - Lancer `dataInitializer.init()` uniquement si :
    - Les versions diffèrent
    - La table `Champion` est vide

---

## 2. Initialisation Automatique et Sécurité

**Objectif** : Assurer une initialisation robuste et sécurisée.

- [ ] **Vérification de la base vide**
  - Dans `Initializator.run()`, ajouter :
    ```java
    if (championRepository.count() == 0) {
        // Forcer l'initialisation
    }
    ```

- [ ] **Paramétrage de l'initialisation**
  - Configurer `onlyFirst` via :
    - Un profil Spring
    - Une propriété externe
  - Éviter de brider la base en production

---

## 3. Complétion des Données Manquantes (Enrichissement)

**Objectif** : Corriger les champs incomplets ou incorrects (ex: "Unknown", `null`).

- [ ] **Champ `resource`**
  - Mapper correctement le champ `partype` du JSON Riot :
    - Exemples : "Puits de sang" (Aatrox), "Mana", "Énergie"

- [ ] **Champs `gender`, `species`, `region`**
  - Identifier la source du problème (pourquoi "Unknown" ?)
  - Fallback si Meraki Analytics est indisponible :
    - Déduire les infos si possible
    - Laisser `null` plutôt que "Unknown" (traitement côté Front)

- [ ] **Champ `releaseDate`**
  - Vérifier le formatage de la date depuis Meraki
  - Garantir une valeur non `null` en BDD

---

## 4. Nettoyage et Optimisation du Code

**Objectif** : Améliorer la maintenabilité et la robustesse.

- [ ] **Refactoriser les chemins d'images**
  - Utiliser les enums `Game` et `Locale` pour :
    - Construire dynamiquement les chemins (ex: `/lol/fr_FR/`)
    - Éviter les erreurs de dossiers

- [ ] **Gestion des exceptions**
  - Ajouter un `GlobalExceptionHandler` pour :
    - Transformer les erreurs système en réponses JSON
    - Codes HTTP adaptés :
      - 404 : Champion non trouvé
      - 500 : Erreur IO

---

## 5. Intégration de Nouveaux Jeux (Évolutivité)

**Objectif** : Préparer l'architecture pour d'autres jeux (ex: Valorant).

- [ ] **Préparer `GameDataInitializer`**
  - Interface générique pour :
    - `LolDataInitializer`
    - Futur `ValorantDataInitializer`

- [ ] **Réflexion sur la structure des données**
  - Évaluer si :
    - Utiliser une table générique (`Entity` ou `Character`)
    - Créer des tables dédiées par jeu
  - Privilégier la modularité et la scalabilité
