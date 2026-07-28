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

### 5. Pourquoi 'deny by default' ?
Toutes les nouvelles routes sont protégées automatiquement ; seules les routes explicitement autorisées sont publiques.


## BANK-006 - JWT Authentication

### 1. Pourquoi utiliser AuthenticationManager plutôt que UserDetailsService directement ?

AuthenticationManager orchestre l'authentification complète (providers, PasswordEncoder, vérifications). UserDetailsService ne fait que charger un utilisateur.

---

### 2. Quelle est la différence entre 401 et 403 ?

401 : l'utilisateur n'est pas authentifié (token absent, invalide ou expiré).

403 : l'utilisateur est authentifié mais n'a pas les droits nécessaires.

---

### 3. Pourquoi utiliser OncePerRequestFilter ?

Pour garantir que le filtre JWT ne s'exécute qu'une seule fois par requête.

---

### 4. Pourquoi vérifier que le SecurityContext est vide avant d'authentifier ?

Pour ne pas remplacer une authentification déjà établie par un autre mécanisme de Spring Security.

---

### 5. Pourquoi l'application est-elle configurée en STATELESS ?

Le serveur ne conserve aucune session HTTP. Chaque requête transporte son JWT.

---

### 6. Quel est le rôle d'AuthenticationEntryPoint ?

Centraliser la réponse lorsqu'une authentification échoue (401 Unauthorized).

---

### 7. Pourquoi utiliser AuthenticationManager avec UsernamePasswordAuthenticationToken ?

AuthenticationManager délègue la vérification des identifiants aux AuthenticationProvider configurés.

---

### 8. Pourquoi ne pas appeler UserDetailsService depuis le Controller ?

Le Controller ne contient aucune logique métier. Toute l'authentification est portée par AuthenticationService.

---

### 9. Pourquoi ne pas stocker les rôles comme unique source de vérité dans le JWT ?

Les rôles peuvent évoluer. Les recharger côté serveur garantit que les autorisations appliquées sont les plus récentes.

---

### 10. Pourquoi utiliser un record pour LoginRequest, LoginResponse et ApiErrorResponse ?

Ils sont immuables, concis et parfaitement adaptés aux DTO.