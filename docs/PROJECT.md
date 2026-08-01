# NovaBank - Java Backend Learning

## Vision

Construire une API bancaire professionnelle en Java 21 / Spring Boot
selon des standards d'entreprise.

## État actuel

- Sprint : 2
- Ticket courant : BANK-008
- Terminés :
  - BANK-001 Initialisation
  - BANK-002 Docker + PostgreSQL
  - BANK-003 Flyway
  - BANK-004 JPA
  - BANK-005 Security
  - BANK-006 JWT Authentication
  - BANK-007 User Management
- En cours :
  - Aucun ticket ouvert

## Stack

- Java 21
- Spring Boot
- Maven
- PostgreSQL
- Flyway
- Docker Compose

## Décisions

1. Monolithe modulaire.
2. API uniquement (pas de frontend métier).
3. Kafka/Redis après une V1 stable.

## Arborescence cible

- config
- common
- shared
- security
- customer
- account
- transaction
- transfer
- exception

## Règles

- Une branche Git par ticket.
- Un commit par ticket.
- Pas de logique métier dans les Controllers.
- Les Services portent les cas d'utilisation.
- Les Repositories ne sont jamais appelés depuis les Controllers.

## Définition de terminé

- Compile
- Tests OK
- Documentation à jour
- Commit effectué

## Fonctionnement

Au début d'une nouvelle discussion, écrire simplement :

`Continue NovaBank`

`PROJECT.md` est la référence du projet.

---

# BANK-006 - JWT Authentication

## Fonctionnalités

- Authentification JWT stateless
- Endpoint `POST /auth/login`
- Génération des JWT (HS256)
- Validation des JWT
- `JwtAuthenticationFilter`
- `AuthenticationEntryPoint`
- `ApiErrorResponse`
- `AuthenticationService`
- `AuthenticationController`
- `JwtProperties`
- `PasswordEncoder`
- `AuthenticationManager`
- `UserDetailsService` temporaire en mémoire

## Tests validés

- Login valide
- Login invalide
- Endpoint protégé sans JWT
- Endpoint protégé avec JWT valide
- JWT altéré rejeté

## Décisions d'architecture

- API totalement stateless
- Pas de logique métier dans les Controllers
- `AuthenticationManager` orchestre l'authentification
- `JwtService` est responsable uniquement des JWT
- Gestion centralisée des erreurs de sécurité

---

# BANK-007 - User Management

## Fonctionnalités

- Création de la table `users` avec Flyway
- Entité JPA `User`
- Identifiants en UUID
- `UserRepository`
- `JpaUserDetailsService`
- Remplacement du `InMemoryUserDetailsService`
- Authentification par email
- Mot de passe stocké avec BCrypt
- Utilisateur de démonstration créé par migration Flyway
- Login JWT fonctionnel avec un utilisateur PostgreSQL

## Tests validés

- Authentification avec utilisateur PostgreSQL
- Vérification BCrypt
- Génération du JWT
- Accès à un endpoint protégé avec le JWT
- `mvn clean verify`
- 11 tests exécutés
- 0 échec
- 0 erreur

## Décisions d'architecture

- `User` reste une entité métier et n'implémente pas `UserDetails`
- `JpaUserDetailsService` adapte `User` vers `UserDetails`
- `AuthenticationService` ne communique jamais directement avec `UserRepository`
- `AuthenticationManager` reste le point d'entrée de l'authentification
- L'email est l'identifiant de connexion
- Les JWT ne sont jamais stockés en base
- Les futurs rôles seront ajoutés indépendamment de l'entité `User`
- Les données bancaires (`Customer`, `Account`) restent découplées de l'authentification

## Acquis

À la fin de ce ticket, le projet dispose d'une authentification complète :

- PostgreSQL
- Spring Security
- BCrypt
- JWT
- Flyway
- JPA

Le système est prêt à accueillir la gestion des rôles et des autorisations dans BANK-008.