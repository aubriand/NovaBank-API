# DEVELOPER.md

# Règles de développement NovaBank

## Général

-   Tout le code est en anglais.
-   Les commits suivent Conventional Commits.
-   Une branche Git par ticket.
-   Aucun commit cassant la compilation.

## Java

-   Java 21.
-   Préférer les types immuables lorsque possible.
-   Utiliser BigDecimal pour les montants.
-   Utiliser UUID pour les identifiants métier lorsque demandé.

## Spring

-   Injection par constructeur uniquement.
-   Pas de logique métier dans les Controllers.
-   Les Services représentent les cas d'utilisation.
-   Les Repositories ne sont jamais appelés depuis les Controllers.

## JPA

-   Ne jamais utiliser @Data sur une Entity.
-   Préférer FetchType.LAZY.
-   Toujours réfléchir aux transactions.

## API

-   DTO pour les entrées/sorties.
-   Validation Bean Validation.
-   Gestion centralisée des exceptions.
-   Documentation OpenAPI à maintenir.

## Tests

-   Une fonctionnalité importante doit être testée.
-   Les tests doivent rester lisibles.

## Qualité

Avant chaque commit : - Le projet compile. - Les tests passent. - Le
code est formaté. - Aucun warning important ignoré.
