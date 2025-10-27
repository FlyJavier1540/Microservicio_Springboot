package citasmedicas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity @Getter @Setter
public class Cita {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Paciente paciente;

    @ManyToOne
    private Doctor doctor;

    private LocalDateTime fechaHora;
    private String estado;
    private String notas;
}
