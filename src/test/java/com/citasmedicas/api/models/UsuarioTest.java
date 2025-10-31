package com.citasmedicas.api.models;

import com.citasmedicas.api.enums.RolNombre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas para el Modelo de Entidad Usuario")
class UsuarioTest {

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
    }

    @Test
    @DisplayName("Debería instanciar un Usuario usando el constructor sin argumentos")
    void testNoArgsConstructor() {
        assertNotNull(usuario, "La instancia no debería ser nula.");
    }

    @Test
    @DisplayName("Debería instanciar un Usuario usando el constructor con todos los argumentos")
    void testAllArgsConstructor() {
        // Arrange
        Set<Rol> roles = new HashSet<>();
        roles.add(new Rol(1L, RolNombre.ADMIN));

        // Act
        Usuario usuarioCompleto = new Usuario(1L, "admin@test.com", "password123", roles);

        // Assert
        assertNotNull(usuarioCompleto);
        assertEquals(1L, usuarioCompleto.getId());
        assertEquals("admin@test.com", usuarioCompleto.getEmail());
        assertEquals("password123", usuarioCompleto.getPassword());
        assertEquals(roles, usuarioCompleto.getRoles());
    }

    @Test
    @DisplayName("Debería establecer y obtener el ID, Email y Password correctamente")
    void shouldSetAndGetBasicFields() {
        // Arrange
        Long id = 80L;
        String email = "test@user.com";
        String password = "secure_password";

        // Act
        usuario.setId(id);
        usuario.setEmail(email);
        usuario.setPassword(password);

        // Assert
        assertEquals(id, usuario.getId());
        assertEquals(email, usuario.getEmail());
        assertEquals(password, usuario.getPassword());
    }

    @Test
    @DisplayName("Debería establecer y obtener los Roles correctamente")
    void shouldSetAndGetRoles() {
        // Arrange
        Set<Rol> roles = new HashSet<>();
        roles.add(new Rol(2L, RolNombre.DOCTOR));

        // Act
        usuario.setRoles(roles);

        // Assert
        assertEquals(roles, usuario.getRoles());
        assertFalse(usuario.getRoles().isEmpty());
    }

    @Test
    @DisplayName("getUsername debería devolver el email del usuario")
    void getUsername_ShouldReturnEmail() {
        // Arrange
        String email = "username@test.com";
        usuario.setEmail(email);

        // Act & Assert
        assertEquals(email, usuario.getUsername());
    }

    @Test
    @DisplayName("getAuthorities debería devolver los roles con el prefijo 'ROLE_'")
    void getAuthorities_ShouldReturnPrefixedRoles() {
        // Arrange
        Set<Rol> roles = new HashSet<>();
        roles.add(new Rol(1L, RolNombre.ADMIN));
        roles.add(new Rol(2L, RolNombre.DOCTOR));
        usuario.setRoles(roles);

        // Act
        Collection<? extends GrantedAuthority> authorities = usuario.getAuthorities();

        // Assert
        assertNotNull(authorities);
        assertEquals(2, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_DOCTOR")));
    }

    @Test
    @DisplayName("getAuthorities debería devolver una lista vacía si no hay roles")
    void getAuthorities_ShouldReturnEmptyList_WhenNoRoles() {
        // Arrange
        usuario.setRoles(new HashSet<>());

        // Act
        Collection<? extends GrantedAuthority> authorities = usuario.getAuthorities();

        // Assert
        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }

    @Test
    @DisplayName("Los métodos de estado de la cuenta de UserDetails deberían devolver true")
    void userDetailsAccountStatusMethods_ShouldReturnTrue() {
        assertTrue(usuario.isAccountNonExpired(), "isAccountNonExpired should be true");
        assertTrue(usuario.isAccountNonLocked(), "isAccountNonLocked should be true");
        assertTrue(usuario.isCredentialsNonExpired(), "isCredentialsNonExpired should be true");
        assertTrue(usuario.isEnabled(), "isEnabled should be true");
    }
}