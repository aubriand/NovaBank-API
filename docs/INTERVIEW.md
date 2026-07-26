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
