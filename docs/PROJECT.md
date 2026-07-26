# PROJECT.md

# NovaBank - Java Backend Learning

## Vision

Construire une API bancaire professionnelle en Java 21 / Spring Boot
selon des standards d'entreprise.

## Etat actuel

-   Sprint : 2
-   Ticket courant : BANK-005
-   Terminés :
    -   BANK-001 Initialisation
    -   BANK-002 Docker + PostgreSQL
    -   BANK-003 Flyway
    -   BANK-004 JPA
-   En cours :
    -   BANK-005 Security

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
-   Pas de logique métier dans les controllers.
-   Les services portent les cas d'utilisation.
-   Les repositories ne sont jamais appelés directement depuis les
    controllers.

## Définition de terminé

-   Compile
-   Tests OK
-   Documentation à jour
-   Commit effectué

## Fonctionnement

Au début d'une nouvelle discussion, écrire simplement : 'Continue
NovaBank'.

PROJECT.md est la référence et sera mis à jour après chaque ticket.
