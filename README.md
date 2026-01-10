# Microservices Library Application

**Auteur:** ELFATIHI Mohamed

## Description du Projet

Ce projet est une application de gestion de bibliothèque basée sur une architecture microservices avec Spring Boot, Eureka, Kafka et MySQL.

### Objectifs du Projet

Étendre l'application existante afin de :
- Ajouter une persistance MySQL pour chaque service métier
- Introduire Kafka pour la communication asynchrone
- Implémenter un Notification Service découplé, basé sur Kafka
- Déployer via Docker Compose

## Architecture

### Microservices

1. **Eureka Server** - Découverte et enregistrement des services
2. **Gateway Service** - Point d'entrée unique et routage dynamique
3. **User Service** - Gestion des utilisateurs
4. **Book Service** - Gestion des livres
5. **Emprunt Service** - Gestion des emprunts
6. **Notification Service** - Gestion des notifications asynchrones via Kafka

### Base de Données (Database per Service)

Chaque microservice possède sa propre base de données MySQL :
- `user-service` → `db_user` (port 3307)
- `book-service` → `db_book` (port 3308)
- `emprunt-service` → `db_emprunter` (port 3309)

### Communication Asynchrone avec Kafka

**Topic Kafka:** `emprunt-created`

**Producteur:** `emprunt-service`  
**Consommateur:** `notification-service`

**Structure du message:**
```json
{
  "empruntId": 1,
  "userId": 3,
  "bookId": 5,
  "eventType": "EMPRUNT_CREATED",
  "timestamp": "2025-01-01T14:00:00"
}
```

## Technologies Utilisées

- Java 17+
- Spring Boot 3.4.1
- Spring Cloud (Eureka, Gateway)
- Spring Data JPA
- MySQL 8
- Apache Kafka
- Docker & Docker Compose
- Maven

## Prérequis

- Docker (version 20.x ou supérieure)
- Docker Compose (version 3.8 ou supérieure)

## Installation et Exécution

### 1. Cloner le Projet

```bash
git clone <votre-repo-git>
cd microservicesapp
```

### 2. Démarrer tous les Services avec Docker Compose

```bash
docker-compose up --build
```

Le démarrage complet peut prendre 2-3 minutes. Attendez que tous les services soient enregistrés dans Eureka.

### 3. Vérifier que tous les Services sont Démarrés

```bash
docker-compose ps
```

Tous les services devraient avoir le statut "Up".

## Vérification des Services

### Eureka Dashboard
Vérifiez que tous les services sont enregistrés :
```
http://localhost:8761
```

Vous devriez voir :
- EUREKA-SERVICE
- GATEWAY-SERVICE
- USER-SERVICE
- BOOK-SERVICE
- EMPRUNT-SERVICE

### Gateway Health Check
```bash
curl http://localhost:9999/actuator/health
```

**Réponse attendue:**
```json
{
  "status": "UP"
}
```

## Tests des Endpoints

### User Service

**Créer un Utilisateur**
```bash
curl -X POST http://localhost:9999/user-service/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Mohamed Elfatihi",
    "email": "mohamed@example.com"
  }'
```

**Récupérer tous les Utilisateurs**
```bash
curl http://localhost:9999/user-service/api/users
```

**Récupérer un Utilisateur par ID**
```bash
curl http://localhost:9999/user-service/api/users/1
```

### Book Service

**Créer un Livre**
```bash
curl -X POST http://localhost:9999/book-service/api/books/add \
  -H "Content-Type: application/json" \
  -d '{
    "titre": "Clean Code"
  }'
```

**Récupérer tous les Livres**
```bash
curl http://localhost:9999/book-service/api/books/all
```

**Récupérer un Livre par ID**
```bash
curl http://localhost:9999/book-service/api/books/1
```

### Emprunt Service

**Créer un Emprunt (déclenche une notification Kafka)**
```bash
curl -X POST http://localhost:9999/emprunt-service/emprunts/1/1
```

Cette action crée un emprunt et publie automatiquement un événement sur Kafka.

**Récupérer tous les Emprunts**
```bash
curl http://localhost:9999/emprunt-service/emprunts
```

## Vérification du Notification Service et Kafka

### Vérifier les Logs du Notification Service

```bash
docker logs notification-service
```

Après avoir créé un emprunt, vous devriez voir un message de notification avec les détails de l'emprunt.

### Vérifier les Topics Kafka

```bash
# Lister les topics
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092

# Afficher les messages du topic emprunt-created
docker exec -it kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic emprunt-created \
  --from-beginning
```

## Accès aux Bases de Données MySQL

### User Database
```bash
docker exec -it mysql-user mysql -uroot -proot db_user
```

```sql
SELECT * FROM u;
```

### Book Database
```bash
docker exec -it mysql-book mysql -uroot -proot db_book
```

```sql
SELECT * FROM book;
```

### Emprunt Database
```bash
docker exec -it mysql-emprunt mysql -uroot -proot db_emprunter
```

```sql
SELECT * FROM emprunter;
```

## Configuration des Services

### Ports Utilisés

| Service             | Port  |
|---------------------|-------|
| Eureka Server       | 8761  |
| Gateway             | 9999  |
| User Service        | 8082  |
| Book Service        | 8081  |
| Emprunt Service     | 8083  |
| MySQL User          | 3307  |
| MySQL Book          | 3308  |
| MySQL Emprunt       | 3309  |
| Kafka               | 9092  |
| Zookeeper           | 2181  |

## Arrêter l'Application

```bash
# Arrêter tous les services
docker-compose down

# Arrêter et supprimer les volumes (données effacées)
docker-compose down -v
```

## Dépannage

### Les services ne démarrent pas

```bash
# Vérifier les logs
docker-compose logs <service-name>
```

### Les tables ne sont pas créées dans MySQL

```bash
# Vérifier si MySQL est prêt
docker exec -it mysql-user mysql -uroot -proot -e "SELECT 1;"

# Vérifier les logs du service
docker logs user-service | grep -i "create table"
```

### Kafka ne fonctionne pas

```bash
# Vérifier Kafka
docker logs kafka

# Vérifier la santé de Kafka
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092
```

### Rebuild complet

```bash
docker-compose down -v
docker-compose build --no-cache
docker-compose up
```

## Auteur

**ELFATIHI Mohamed**

Date : Janvier 2026
