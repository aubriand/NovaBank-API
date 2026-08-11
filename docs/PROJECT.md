# NovaBank - Java Backend Learning

## Vision

Construire une API bancaire professionnelle en Java 21 / Spring Boot
selon des standards d'entreprise.

## État actuel

- Sprint : 2
- Ticket courant : BANK-013
- Terminés :
  - BANK-001 Initialisation
  - BANK-002 Docker + PostgreSQL
  - BANK-003 Flyway
  - BANK-004 JPA
  - BANK-005 Security
  - BANK-006 JWT Authentication
  - BANK-007 User Management
  - BANK-008 Roles & Authorizations
  - BANK-009 Customer Management
  - BANK-010 Account Management
  - BANK-011 Transaction Management
  - BANK-012 Transfer Management
- En cours :
  - Aucun ticket ouvert

## Stack

-   Java 21
-   Spring Boot
-   Maven
-   PostgreSQL
-   Flyway
-   Docker Compose

## Décisions

1.  Monolithe modulaire.
2.  API uniquement (pas de frontend métier).
3.  Kafka/Redis après une V1 stable.

## Arborescence cible

-   config
-   common
-   shared
-   security
-   customer
-   account
-   transaction
-   transfer
-   exception

## Règles

-   Une branche Git par ticket.
-   Un commit par ticket.
-   Pas de logique métier dans les Controllers.
-   Les Services portent les cas d'utilisation.
-   Les Repositories ne sont jamais appelés depuis les Controllers.

## Définition de terminé

-   Compile
-   Tests OK
-   Documentation à jour
-   Commit effectué

## Fonctionnement

Au début d'une nouvelle discussion, écrire simplement :

`Continue NovaBank`

`PROJECT.md` est la référence du projet.

------------------------------------------------------------------------

# BANK-006 - JWT Authentication

## Fonctionnalités

-   Authentification JWT stateless
-   Endpoint `POST /auth/login`
-   Génération des JWT (HS256)
-   Validation des JWT
-   `JwtAuthenticationFilter`
-   `AuthenticationEntryPoint`
-   `ApiErrorResponse`
-   `AuthenticationService`
-   `AuthenticationController`
-   `JwtProperties`
-   `PasswordEncoder`
-   `AuthenticationManager`
-   `UserDetailsService` temporaire en mémoire

## Tests validés

-   Login valide
-   Login invalide
-   Endpoint protégé sans JWT
-   Endpoint protégé avec JWT valide
-   JWT altéré rejeté

## Décisions d'architecture

-   API totalement stateless
-   Pas de logique métier dans les Controllers
-   `AuthenticationManager` orchestre l'authentification
-   `JwtService` est responsable uniquement des JWT
-   Gestion centralisée des erreurs de sécurité

------------------------------------------------------------------------

# BANK-007 - User Management

## Fonctionnalités

-   Création de la table `users` avec Flyway
-   Entité JPA `User`
-   Identifiants en UUID
-   `UserRepository`
-   `JpaUserDetailsService`
-   Remplacement du `InMemoryUserDetailsService`
-   Authentification par email
-   Mot de passe stocké avec BCrypt
-   Utilisateur de démonstration créé par migration Flyway
-   Login JWT fonctionnel avec un utilisateur PostgreSQL

## Tests validés

-   Authentification avec utilisateur PostgreSQL
-   Vérification BCrypt
-   Génération du JWT
-   Accès à un endpoint protégé avec le JWT
-   `mvn clean verify`
-   11 tests exécutés
-   0 échec
-   0 erreur

## Décisions d'architecture

-   `User` reste une entité métier et n'implémente pas `UserDetails`
-   `JpaUserDetailsService` adapte `User` vers `UserDetails`
-   `AuthenticationService` ne communique jamais directement avec
    `UserRepository`
-   `AuthenticationManager` reste le point d'entrée de
    l'authentification
-   L'email est l'identifiant de connexion
-   Les JWT ne sont jamais stockés en base
-   Les futurs rôles seront ajoutés indépendamment de l'entité `User`
-   Les données bancaires (`Customer`, `Account`) restent découplées de
    l'authentification

## Acquis

À la fin de ce ticket, le projet dispose d'une authentification complète
:

-   PostgreSQL
-   Spring Security
-   BCrypt
-   JWT
-   Flyway
-   JPA

# BANK-008 - Roles & Authorizations

## Fonctionnalités

-   Ajout de l'enum `UserRole` (`USER`, `ADMIN`)
-   Stockage du rôle dans la table `users`
-   Mapping JPA avec `EnumType.STRING`
-   Migration Flyway pour ajouter la colonne `role`
-   `JpaUserDetailsService` construit les authorities à partir du rôle
    en base
-   Protection des endpoints avec `hasRole` / `hasAnyRole`
-   `JwtAccessDeniedHandler` pour les réponses 403
-   Distinction entre erreurs 401 et 403

## Tests validés

-   Endpoint protégé sans JWT → 401
-   Endpoint accessible avec rôle USER → 200
-   Endpoint ADMIN avec JWT USER → 403
-   Endpoint ADMIN avec JWT ADMIN → 200
-   `mvn clean verify` réussi

## Décisions d'architecture

-   Les rôles sont persistés en base.
-   Les rôles sont représentés par un enum Java.
-   Les enums sont stockés avec `EnumType.STRING`.
-   Les authorities Spring sont construites à partir du rôle métier.
-   `AuthenticationEntryPoint` traite les erreurs d'authentification
    (401).
-   `AccessDeniedHandler` traite les erreurs d'autorisation (403).

# BANK-009 - Customer Management

## Fonctionnalités

-   Création de la table `customers` avec Flyway
-   Entité JPA `Customer`
-   Identifiants métier en UUID
-   `CustomerRepository` avec Spring Data JPA
-   `CustomerService` pour les cas d'utilisation
-   DTO `CreateCustomerRequest` et `CustomerResponse`
-   Endpoint `POST /customers`
-   Endpoint `GET /customers/{id}`
-   Validation Bean Validation
-   `CustomerNotFoundException`
-   Gestion centralisée des erreurs avec `GlobalExceptionHandler`

## Tests validés

-   Création valide → 201
-   Création invalide → 400
-   Consultation d'un client existant → 200
-   Consultation d'un client inexistant → 404
-   `mvn clean verify` réussi
-   18 tests exécutés
-   0 échec
-   0 erreur

## Décisions d'architecture

-   `Customer` reste indépendant de l'entité de sécurité `User`.
-   Les Controllers n'accèdent jamais directement aux Repositories.
-   Les Entities JPA ne sont pas exposées directement dans l'API.
-   Les DTO définissent les frontières HTTP.
-   Bean Validation est appliquée aux DTO d'entrée.
-   Les exceptions applicatives restent indépendantes de HTTP.
-   `GlobalExceptionHandler` traduit les exceptions applicatives en
    réponses HTTP.
-   Les codes postaux sont représentés comme des chaînes de caractères.
-   Les UUID ne remplacent pas les contrôles d'autorisation.

# BANK-010 - Account Management

## Fonctionnalités

-   Création de la table `accounts` avec Flyway
-   Entité JPA `Account`
-   Identifiants en UUID
-   Relation `Account` → `Customer` avec
    `@ManyToOne(fetch = FetchType.LAZY)`
-   Clé étrangère `customer_id` non nullable
-   Solde représenté avec `BigDecimal`
-   Solde initial imposé à zéro côté serveur
-   Stockage PostgreSQL du solde avec un type `NUMERIC`
-   `AccountRepository` avec Spring Data JPA
-   `AccountService` pour les cas d'utilisation
-   DTO `CreateAccountRequest` et `AccountResponse`
-   Génération serveur d'un identifiant bancaire interne préfixé par
    `NB`
-   Endpoint `POST /accounts`
-   Endpoint `GET /accounts/{id}`
-   Validation Bean Validation
-   `AccountNotFoundException`

## Tests validés

-   Création avec un client existant → 201
-   Création avec un client inexistant → 404
-   Création avec `customerId` null → 400
-   Consultation d'un compte existant → 200
-   Consultation d'un compte inexistant → 404
-   Vérification du solde initial à zéro
-   `mvn clean verify` réussi
-   23 tests exécutés
-   0 échec
-   0 erreur

## Décisions d'architecture

-   Un `Customer` peut posséder plusieurs comptes ; `Account` référence
    donc `Customer` avec une relation `ManyToOne`.
-   La relation vers `Customer` est chargée en `LAZY`.
-   Les montants monétaires utilisent `BigDecimal`.
-   Le client HTTP ne contrôle pas le solde initial.
-   `AccountResponse` expose `customerId` et non l'entité `Customer`.
-   Les Controllers n'accèdent jamais directement aux Repositories.

# BANK-011 - Transaction Management

## Fonctionnalités

-   Création de la table `transactions` avec Flyway
-   Entité JPA `Transaction`
-   Enum `TransactionType` avec `DEPOSIT` et `WITHDRAWAL`
-   Stockage de l'enum avec `EnumType.STRING`
-   Relation `Transaction` → `Account` avec
    `@ManyToOne(fetch = FetchType.LAZY)`
-   Montants représentés avec `BigDecimal`
-   `TransactionRepository`
-   `TransactionService`
-   DTO `CreateTransactionRequest` et `TransactionResponse`
-   Endpoint `POST /accounts/{accountId}/transactions`
-   Endpoint `GET /accounts/{accountId}/transactions`
-   Dépôt sur un compte
-   Retrait sur un compte
-   Historique des transactions d'un compte
-   Validation des montants avec Bean Validation
-   `InsufficientBalanceException`
-   Réponse HTTP 409 en cas de solde insuffisant

## Tests validés

-   Dépôt valide → 201 et solde augmenté
-   Retrait valide → 201 et solde diminué
-   Retrait supérieur au solde → 409
-   Solde inchangé après un retrait refusé
-   Aucune nouvelle transaction créée après un retrait refusé
-   Montant null → 400
-   Montant négatif → 400
-   Compte inexistant → 404
-   Consultation de l'historique → 200
-   Vérification du contenu de l'historique
-   `mvn clean verify` réussi
-   31 tests exécutés
-   0 échec
-   0 erreur

## Décisions d'architecture

-   `TransactionService.create()` définit la frontière transactionnelle
    du cas d'utilisation.
-   La modification du solde et l'enregistrement de la transaction sont
    atomiques.
-   `Account` protège lui-même l'invariant de solde via `withdraw()`.
-   `TransactionService` orchestre les opérations `deposit()` et
    `withdraw()` sans manipuler directement le solde.
-   `BigDecimal` est utilisé pour tous les montants financiers.
-   Les opérations `BigDecimal` sont réaffectées car `BigDecimal` est
    immuable.
-   `TransactionType` est persisté sous forme textuelle avec
    `EnumType.STRING`.
-   `TransactionResponse` expose `accountId` plutôt que l'entité JPA
    `Account`.
-   La relation `Transaction` → `Account` reste unidirectionnelle et
    `LAZY`.
-   `409 Conflict` représente un retrait syntaxiquement valide mais
    incompatible avec l'état actuel du compte.
-   Bean Validation protège le contrat d'entrée ; les invariants métier
    restent dans le domaine.

# BANK-012 - Transfer Management

## Fonctionnalités

- Création de la table `transfers` avec Flyway
- Entité JPA `Transfer`
- Relations `ManyToOne` LAZY vers les comptes source et destination
- Montants en `BigDecimal`
- Timestamp de création du transfert
- Contraintes SQL sur le montant positif et l'interdiction d'un transfert vers le même compte
- `TransferRepository` avec Spring Data JPA
- DTO `CreateTransferRequest` et `TransferResponse`
- Endpoint `POST /transfers`
- `TransferService` pour orchestrer le cas d'utilisation
- Débit du compte source via `Account.withdraw`
- Crédit du compte destination via `Account.deposit`
- `SameAccountTransferException`
- Gestion centralisée des erreurs métier
- Transaction Spring couvrant l'intégralité du transfert

## Tests validés

- Transfert valide → 201
- Débit correct du compte source
- Crédit correct du compte destination
- Transfert persisté
- Compte source inexistant → 404
- Compte destination inexistant → 404
- Montant invalide → 400
- Même compte source et destination → 409
- Solde insuffisant → 409
- Rollback transactionnel vérifié lorsqu'une erreur survient après modification des soldes
- `mvn clean verify` réussi
- Suite de tests entièrement verte

## Décisions d'architecture

- Un transfert constitue un cas d'utilisation métier autonome.
- `TransferService` orchestre le transfert sans manipuler directement les soldes.
- Les invariants de solde restent portés par `Account.withdraw` et `Account.deposit`.
- La frontière `@Transactional` englobe le débit, le crédit et la persistance du transfert.
- Une exception remontant pendant le transfert annule l'ensemble de l'opération.
- Les Controllers ne contiennent aucune logique métier et n'accèdent pas aux Repositories.
- Les Entities JPA ne sont pas exposées directement par l'API.
- Les UUID sont comparés par valeur avec `equals`, et non avec `==`.
- Les contraintes métier simples sont également protégées au niveau PostgreSQL.
- `RuntimeException` déclenche par défaut le rollback Spring ; aucun `rollbackFor` redondant n'est nécessaire.
