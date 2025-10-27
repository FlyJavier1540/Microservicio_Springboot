package citasmedicas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity @Getter @Setter
public class Paciente {
    @Id @GeneratedValue
    private Long id;

    private String nombre;
    private String correo;
    private String telefono;
    private LocalDate fechaNacimiento;
}

