# INTERVIEW.md

## BANK-005 - Spring Security

### 1. Pourquoi SecurityFilterChain remplace-t-il WebSecurityConfigurerAdapter ?
Spring Security privilégie une configuration déclarative par beans plutôt que l'héritage. Cela rend la configuration plus explicite, composable et testable.

### 2. Pourquoi injecter PasswordEncoder ?
Pour réduire le couplage, centraliser la configuration et faciliter les tests.

### 3. HTTP Basic vs JWT
HTTP Basic renvoie les identifiants à chaque requête. JWT envoie un token signé obtenu après authentification. En mode stateless, le serveur ne conserve pas de session HTTP.

### 4. Pourquoi désactiver CSRF ?
Le CSRF protège principalement les authentifications basées sur des cookies. Avec un JWT transmis dans l'en-tête Authorization, cette protection est généralement inutile.

### 5. Pourquoi "deny by default" ?
Toutes les nouvelles routes sont protégées automatiquement ; seules les routes explicitement autorisées sont publiques.

## BANK-006 - JWT Authentication

### 1. Pourquoi utiliser AuthenticationManager plutôt que UserDetailsService directement ?
`AuthenticationManager` orchestre l'authentification complète : sélection des providers, chargement de l'utilisateur, comparaison du mot de passe et vérifications du compte. `UserDetailsService` ne fait que charger un utilisateur.

### 2. Quelle est la différence entre 401 et 403 ?
401 : l'utilisateur n'est pas authentifié, par exemple si le token est absent, invalide ou expiré.

403 : l'utilisateur est authentifié mais ne possède pas les droits nécessaires.

### 3. Pourquoi utiliser OncePerRequestFilter ?
Pour garantir que le filtre JWT ne s'exécute qu'une seule fois par requête.

### 4. Pourquoi vérifier que le SecurityContext est vide avant d'authentifier ?
Pour ne pas remplacer une authentification déjà établie par un autre mécanisme de Spring Security.

### 5. Pourquoi l'application est-elle configurée en STATELESS ?
Le serveur ne conserve aucune session HTTP. Chaque requête transporte son JWT.

### 6. Quel est le rôle d'AuthenticationEntryPoint ?
Centraliser la réponse lorsqu'une authentification échoue avec un statut 401 Unauthorized.

### 7. Pourquoi utiliser AuthenticationManager avec UsernamePasswordAuthenticationToken ?
`AuthenticationManager` délègue la vérification des identifiants aux `AuthenticationProvider` configurés.

### 8. Pourquoi ne pas appeler UserDetailsService depuis le Controller ?
Le controller ne contient aucune logique métier. Toute l'authentification est portée par `AuthenticationService`.

### 9. Pourquoi ne pas stocker les rôles comme unique source de vérité dans le JWT ?
Les rôles peuvent évoluer. Les recharger côté serveur garantit que les autorisations appliquées sont à jour.

### 10. Pourquoi utiliser un record pour LoginRequest, LoginResponse et ApiErrorResponse ?
Ils sont immuables, concis et adaptés aux DTO.

## BANK-007 - User Management

### 1. Pourquoi créer JpaUserDetailsService au lieu d'injecter UserRepository dans AuthenticationService ?
`JpaUserDetailsService` respecte le contrat attendu par Spring Security et isole l'accès aux utilisateurs persistés. `AuthenticationService` reste concentré sur le cas d'utilisation de connexion et délègue l'authentification à `AuthenticationManager`. Cela évite de contourner les providers, le `PasswordEncoder` et les vérifications de compte.

### 2. Pourquoi l'entité User n'implémente-t-elle pas directement UserDetails ?
Pour éviter de coupler le modèle de persistance à Spring Security. L'entité représente les données stockées en base, tandis que `UserDetails` représente le principal de sécurité. `JpaUserDetailsService` joue le rôle d'adaptateur entre les deux.

### 3. Pourquoi stocker un hash BCrypt plutôt que le mot de passe en clair ?
Un mot de passe ne doit jamais être récupérable depuis la base. BCrypt produit un hash lent et salé, ce qui limite l'efficacité des attaques par brute force et empêche deux mots de passe identiques d'avoir systématiquement le même hash. Lors du login, Spring compare le mot de passe reçu au hash avec `PasswordEncoder.matches`.

### 4. Quel est le rôle d'AuthenticationManager ?
`AuthenticationManager` orchestre la chaîne d'authentification. Il reçoit un objet `Authentication`, sélectionne un `AuthenticationProvider`, charge l'utilisateur via `UserDetailsService`, vérifie le mot de passe avec `PasswordEncoder` et retourne une authentification validée ou lève une exception.

### 5. Pourquoi retourner Optional<User> depuis UserRepository ?
Parce qu'une recherche par email peut ne rien retourner. `Optional` représente explicitement cette absence, évite le retour de `null` et oblige l'appelant à gérer le cas, par exemple avec `orElseThrow`.
