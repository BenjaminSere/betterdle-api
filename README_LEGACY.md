# REST API for BetterDLE  

L’objectif de ce dépot est de développer une API REST pour centraliser et exposer des données sur les éléments et personnages de jeux vidéo, cette API sera utiliser dans mon projet BetterDLE. Je sais pertinemment que Java + Spring avec PostgreSQL n’est pas la solution la plus légère pour un projet de cette taille  c’est un peu comme utiliser un canon pour tuer une mouche. Des alternatives comme FastAPI avec SQLite, ou même un simple `fetch` depuis des API tierces, auraient été plus rapides et plus adaptées.

Mais ce n’est pas l’objectif.
Mon but est de montrer ce dont je suis capable en simulant un environnement proche du monde professionnel, avec ses contraintes et ses complexités. Aujourd’hui, les applications ne fonctionnent que rarement seules : elles s’appuient sur des services tiers, des bases de données externalisées, ou des architectures multi-services. J’ai choisi de ne pas me limiter à un simple `fetch` pour m’entraîner à concevoir un système qui communique correctement entre ses composants, même à l’échelle d’un projet personnel.

Côté base de données, j’ai conçu le schéma via JPA, ce qui m’a permis d’automatiser la génération des tables et de me concentrer sur la logique métier. J’ai aussi implémenté des requêtes SQL avancées (jointures complexes, agrégations) pour répondre à des besoins statistiques, bien au-delà du simple CRUD. Ces défis m’ont appris que SQL ne s’improvise pas  et que la performance d’une requête peut faire la différence entre une API réactive et une application lente.

Pour le déploiement et les tests, j’utilise Docker et Docker Compose. Cela me permet de simuler une infrastructure multi-services, avec ses joies (communication entre conteneurs) et ses défis (debugging quand un service refuse de démarrer). Même à petite échelle, cette approche m’a confronté à des problématiques réelles : gestion des dépendances, isolation des services, et surtout, la nécessité de documenter et d’automatiser chaque étape pour éviter les erreurs humaines.

La sécurité et l’hébergement sont des aspects que je prends très au sérieux. Passer d’un projet perso à une mise en production, même modeste, implique de gérer des risques concrets : configuration des pare-feux, gestion des accès, chiffrement des données, etc. J’ai choisi de me confronter à ces défis en explorant deux options :
- Un hébergement chez un fournisseur cloud (pour bénéficier de bonnes pratiques intégrées).
- Un déploiement sur mon homelab (pour apprendre à gérer moi-même la sécurité, le HTTPS via Let’s Encrypt, et les joies des nuits blanches à cause d’un pare-feu mal configuré).

L’objectif n’est pas de tout maîtriser du premier coup, mais d’apprendre en situation réelle  avec la conscience que chaque choix a des conséquences.

En ce qui concerne les IA génératives, je n’étais pas obligé d’aborder ce sujet dans un projet personnel. Mais je préfère être clair et honnête : si je veux que mon travail reflète mes compétences et mes valeurs, je ne vois pas l’intérêt d’être hypocrite.

Il y a un obstacle majeur, pour juger de la pertinence des réponses d’une IA, il faut déjà maîtriser un minimum le sujet. Or, en phase d’apprentissage, utiliser une IA pour coder ou résoudre des problèmes à ma place reviendrait à me tirer une balle dans le pied : moins de réflexion, moins de compréhension, moins de rétention. L’atrophie cognitive induite par les IAG dans l’enseignement est déjà documentée  et c’est un risque que je refuse de prendre.

Je ne suis pas "contre" l’IA : je m’en sers pour gagner du temps sur des tâches ingrates (Rédaction de la documentation, correction de syntaxe). Mais je me fixe des limites strictes :
- Jamais pour la réflexion, la conception ou la résolution de problèmes  car c’est en me confrontant à ces défis que je développe mes compétences.
- Toujours en vérifiant et adaptant ses suggestions, pour ne pas devenir dépendant.

Des études récentes confirment ces risques :
- [Maintenir des zones "sans IA" : préserver des espaces d’apprentissage autonome](https://arxiv.org/abs/2506.08872)
- [Impact des IAG sur la mémoire et la résolution de problèmes](https://pmc.ncbi.nlm.nih.gov/articles/PMC11020077/)
- [Dépendance aux outils d’IA dans l’éducation](https://dl.acm.org/doi/10.1145/3706598.3713778)

Pour finir ce projet reflète mes compétences en devenir, ma curiosité, et surtout mon envie d’apprendre en me confrontant à des défis concrets. Je suis ouvert à tous les retours et conseils pour l’améliorer  d’ailleurs, voici les axes que j’ai déjà identifiés :
- TODO

N’hésitez pas à me donner votre avis  surtout si vous voyez d’autres pistes d’amélioration !

## **Stack Technique**

- **Framework** : Spring Boot **3.2.10** (LTS, maintenue jusqu’en novembre 2027)
- **Langage** : Java **17**
- **Base de données** : PostgreSQL (lancée via Docker)
- **Packaging** : JAR (exécutable avec `java -jar`)

### **Dépendances Maven (Spring Boot)**
| Dépendance               | Utilisation                                  |
|--------------------------|---------------------------------------------|
| Spring Web               | Gestion des requêtes HTTP et REST            |
| Spring Boot DevTools      | Rechargement automatique du code           |
| Spring Data JPA          | Persistance des données avec JPA/Hibernate  |
| Lombok                   | Réduction du code boilerplate (getters, setters, etc.) |
| Validation               | Validation des données (annotations `@Valid`) |
| PostgreSQL Driver         | Connexion à la base de données PostgreSQL    |


# **Lancement de l’application et de la base de données**

## **1. Démarrer l’application Spring Boot**
```bash
mvn spring-boot:run
```
> **Note** : Assurez-vous que la base de données PostgreSQL est démarrée avant de lancer l’application.


## **2. Lancer PostgreSQL avec Docker**

### **Récupérer l’image PostgreSQL**
```bash
docker pull postgres:latest
```

### **Démarrer le conteneur**
```bash
docker run -d \
  --name postgres-dev \
  -e POSTGRES_DB=betterdle \
  -e POSTGRES_USER=betterdle \
  -e POSTGRES_PASSWORD=moi \
  -p 5432:5432 \
  -v pgdata:/var/lib/postgresql/data \
  postgres:latest
```
> **Explications** :
> - `-d` : mode détaché (en arrière-plan)
> - `--name postgres-dev` : nom du conteneur
> - `-e` : variables d’environnement (BDD, utilisateur, mot de passe)
> - `-p 5432:5432` : mapping du port (hôte:conteneur)
> - `-v pgdata:/var/lib/postgresql/data` : volume persistant pour les données

### **Arrêter le conteneur**
```bash
docker rm -f postgres-dev
```

> **Tutoriel recommandé** : [Docker & PostgreSQL - Configuration de base](https://youtu.be/24Jya6Qunhk)

---

# **Accès à la base de données**

## **1. Se connecter au conteneur PostgreSQL**
```bash
docker exec -it postgres-dev psql -U betterdle -d betterdle
```

## **2. Se connecter en local (sans Docker)**
> **Prérequis** : PostgreSQL installé localement.
```bash
psql -h localhost -p 5432 -U betterdle -d betterdle
```

# **Gestion des données**

### **Mise à jour automatique des données**
- Une **tâche planifiée** (`@Scheduled`) s’exécute **tous les jours à 4h** pour :
  1. Récupérer les données depuis l’API Riot (ex: [DDragon](https://developer.riotgames.com/docs/lol)) via une requête HTTP.
  2. Désérialiser les données en **DTO** (ex: `ChampionRootDTO`) avec Jackson.
  3. Comparer la version du jeu avec celle en base de données.
  4. Si la base est vide ou si une nouvelle version est détectée, les données sont :
     - Mappées en **entités JPA** (ex: `Champion`).
     - Persistées via le **repository Spring Data JPA**, dans une **transaction atomique** (`@Transactional`).
     - En cas d’erreur, aucune donnée n’est sauvegardée (roll-back automatique).

# **Accès à l’API**

### **Sécurité et CORS**
- **Pas d’authentification** pour le moment (l’API est réservée à la webapp BetterDLE).
- La gestion des **origines autorisées (CORS)** est configurée dans :
  - `src/main/java/betterdle/api/config/SecurityConfig.java` (classe `WebMvcConfigurer`).
  - Les origines autorisées sont définies dans `application.properties` :
    ```properties
    app.cors.allowed-origins=http://localhost:3000,https://mon-domaine.com
    ```

> **Pour ajouter une nouvelle origine** :
> Ajoutez simplement l’URL dans la liste `app.cors.allowed-origins` et redémarrez l’application.

# **Remarques et améliorations futures**
- **Authentification** : À l’étude pour une future version (JWT, OAuth2, etc.).
- **Optimisations** : Cache des données, pagination des requêtes, etc.
- **Documentation API** : Swagger/OpenAPI à venir pour faciliter l’intégration.

**Besoin d’aide ou de précisions ?** N’hésitez pas à ouvrir une issue ou à me contacter !

### **Pourquoi cette version est meilleure ?**
- **Clarté** : Les sections sont bien séparées, avec des titres explicites.
- **Précision** : Chaque commande ou concept est expliqué.
- **Professionnalisme** : Le ton est technique mais accessible, avec des exemples concrets.
- **Maintenabilité** : Facile à mettre à jour ou à compléter.
