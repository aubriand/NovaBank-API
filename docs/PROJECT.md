# NovaBank - Java Backend Learning

## Vision

Construire une API bancaire professionnelle en Java 21 / Spring Boot
selon des standards d'entreprise.

## Etat actuel

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
- Pas de logique métier dans les controllers.
- Les services portent les cas d'utilisation.
- Les repositories ne sont jamais appelés directement depuis les controllers.

## Définition de terminé

- Compile
- Tests OK
- Documentation à jour
- Commit effectué

## Fonctionnement

Au début d'une nouvelle discussion, écrire simplement : `Continue NovaBank`.

`PROJECT.md` est la référence et sera mis à jour après chaque ticket.

## BANK-006 - JWT Authentication

### Fonctionnalités

- Authentification JWT stateless
- Endpoint `POST /auth/login`
- Génération des JWT (HS256)
- Validation des JWT
- `JwtAuthenticationFilter`
- `AuthenticationEntryPoint`
- `ApiErrorResponse`
- `AuthenticationService`
- `AuthenticationController`
- `JwtProperties` avec `ConfigurationProperties`
- `PasswordEncoder`
- `AuthenticationManager`
- `UserDetailsService` temporaire en mémoire

### Tests validés

- Login valide
- Login invalide
- Endpoint protégé sans JWT
- Endpoint protégé avec JWT valide
- JWT altéré rejeté

### Décisions d'architecture

- API totalement stateless
- Pas de logique métier dans les controllers
- `AuthenticationManager` responsable de l'authentification
- `JwtService` responsable uniquement du JWT
- Gestion centralisée des erreurs de sécurité

## BANK-007 - User Management

### Fonctionnalités

- Table PostgreSQL `users` créée par Flyway
- Entité JPA `User` avec identifiant UUID
- Recherche d'un utilisateur par email avec `UserRepository`
- Implémentation de `JpaUserDetailsService`
- Remplacement du `UserDetailsService` en mémoire par JPA
- Authentification par email et mot de passe BCrypt
- Utilisateur de démonstration créé par migration Flyway
- Login JWT validé avec un utilisateur stocké en base
- Accès à un endpoint protégé validé avec le JWT généré

### Décisions d'architecture

- L'entité `User` reste indépendante de l'interface Spring Security `UserDetails`
- `JpaUserDetailsService` adapte l'entité persistée vers `UserDetails`
- `AuthenticationService` n'accède jamais directement au repository
- `UserRepository` retourne `Optional<User>` pour représenter explicitement l'absence
- L'email est l'identifiant de connexion NovaBank
- Les tokens JWT ne sont pas stockés dans la table `users`
- Les données client et les comptes bancaires restent séparés de l'utilisateur d'authentification

### Validation

- Ticket validé fonctionnellement
- Compilation et tests à confirmer dans la review GitHub
- Documentation mise à jour
