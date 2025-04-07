package com.ccsw.tutorial.config;

import com.ccsw.tutorial.auth.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Clase principal de configuración de seguridad para Spring Security
 *
 * Define qué rutas están permitidas según el rol (anónimo o administrador)
 * Configura la app como stateless (sin sesiones, se usa token JWT)
 * Habilita CORS para permitir llamadas desde el frontend (Angular)
 * Inserta el filtro JWT para validar el token en cada petición
 */
@Configuration
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    /**
     * Constructor para inyectar el filtro JWT personalizado
     */
    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    /**
     * Cadena de filtros de seguridad para la aplicación
     *
     * Este método configura las reglas de acceso para los distintos endpoints,
     * y activa el filtro personalizado JWT antes del filtro por defecto de login
     *
     * @param http objeto de configuración de seguridad
     * @return filtro configurado
     * @throws Exception si falla la configuración
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Desactivamos CSRF porque usamos una API REST, no formularios
                .csrf(csrf -> csrf.disable())
                // Indicamos que no se guardan sesiones: autenticación con token
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Definimos qué rutas están permitidas y qué roles las pueden usar
                .authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.POST, "/auth/login").permitAll() // login sin autenticación
                        .requestMatchers(HttpMethod.GET, "/**").permitAll().requestMatchers(HttpMethod.POST, "/**").hasRole("ADMIN").requestMatchers(HttpMethod.PUT, "/**").hasRole("ADMIN").requestMatchers(HttpMethod.DELETE, "/**")
                        .hasRole("ADMIN").anyRequest().authenticated())
                // Activamos configuración CORS (para permitir llamadas desde localhost:4200)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Insertamos el filtro JWT antes del de autenticación por formulario
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class).build();
    }

    /**
     * Configuración de CORS para permitir llamadas del frontend Angular
     *
     * @return configuración CORS aplicada a todos los endpoints
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // aplicar a todos los endpoints
        return source;
    }

    /**
     * Codificador de contraseñas con algoritmo BCrypt
     *
     * @return codificador seguro
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager  es el encargado de realizar el proceso de autenticación (login),
     * es decir, validar el nombre de usuario y contraseña
     *
     *
     * @param config configuración de autenticación proporcionada por Spring
     * @return el AuthenticationManager el objeto que Spring usará para comprobar si un usuario y contraseña son válidos en el login
     * @throws Exception si falla la configuración
     */

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
