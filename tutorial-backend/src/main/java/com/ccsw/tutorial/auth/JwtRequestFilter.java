package com.ccsw.tutorial.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * Filtro que intercepta cada petición HTTP y valida el token JWT si está presente
     *
     * Este filtro es registrado por Spring Security desde SecurityConfig y se ejecuta
     * automáticamente en cada request. Si el token es válido, autentica al usuario
     *
     * @param request
     * @param response
     * @param chain     Cadena de filtros que permite continuar con la ejecución del resto de filtros
     * @throws ServletException
     * @throws IOException
     */

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        /**
         * Peticion desde el front:
         *
         * GET /clientes HTTP/1.1
         * Host: localhost:8080
         * Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
         *
         */
        final String authHeader = request.getHeader("Authorization");

        String username = null;
        String token = null;
        //Verificamos si hay un header Authorization con el formato "Bearer <token>"
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            username = jwtUtil.extractUsername(token);// Extraemos el username desde el token
        }

        //Si se obtuvo un username y no hay usuario autenticado todavía
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Cargamos los detalles del usuario desde BBDD
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            //Validamos el token (firma, expiración, y que coincida con el usuario)
            if (jwtUtil.validateToken(token, userDetails)) {
                //Creamos un objeto de autenticación para Spring Security
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                //Auntenticamos al usuario
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        //Para que continue con la peticion o vaya al controlador
        chain.doFilter(request, response);
    }
}
