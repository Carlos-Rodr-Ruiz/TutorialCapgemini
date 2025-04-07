package com.ccsw.tutorial.auth;

import com.ccsw.tutorial.auth.model.AuthRequest;
import com.ccsw.tutorial.auth.model.AuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")// Permite peticiones desde cualquier dominio
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            logger.info("Intentando iniciar sesión con el usuario: {}", request.getUsername());

            // Autenticamos al usuario con Spring Security
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            UserDetails user = (UserDetails) authentication.getPrincipal();
            logger.info("Autenticación exitosa para el usuario: {}", request.getUsername());

            // Generamos el token JWT
            String token = jwtUtil.generateToken(user);
            logger.info("Token generado para el usuario: {}", request.getUsername());

            // Devolvemos el token en un AuthResponse
            return ResponseEntity.ok(new AuthResponse(token));

        } catch (BadCredentialsException e) {
            logger.error("Error de autenticación: Usuario o contraseña incorrectos para el usuario: {}", request.getUsername());
            return ResponseEntity.status(401).body("Usuario o contraseña incorrectos");
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

}
