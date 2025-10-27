package citasmedicas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Entity @Getter @Setter
public class Doctor {
    @Id @GeneratedValue
    private Long id;

    private String nombre;
    private String especialidad;
    private String correo;
    private String telefono;
}
