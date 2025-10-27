package citasmedicas.controller;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsuarioController {

    @GetMapping("/perfil")
    public String perfil(OAuth2AuthenticationToken auth){
        String username = auth.getPrincipal().getAttribute("FlyJavier1540");
        String email = auth.getPrincipal().getAttribute(null);
        return "Hola" + username;
    }

    /*@GetMapping("/public/saludo")
    public String saludo(){
        return "Esta ruta es pública. No necesitas login.";
    }*/
}
