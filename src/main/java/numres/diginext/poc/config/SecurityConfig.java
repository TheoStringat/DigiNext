package numres.diginext.poc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Désactiver CSRF pour éviter les erreurs sur les requêtes POST
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/analyze", "/static/**", "/templates/**").permitAll() // Autoriser les pages publiques
                        .anyRequest().authenticated() // Sécuriser toutes les autres pages
                )
                .formLogin(login -> login.disable()) // Désactiver le formulaire de login par défaut
                .httpBasic(httpBasic -> httpBasic.disable()); // Désactiver l'authentification basique

        return http.build();
    }
}
