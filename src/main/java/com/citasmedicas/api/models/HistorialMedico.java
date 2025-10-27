package com.citasmedicas.api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "historiales_medicos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistorialMedico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String diagnostico;

    @Column(columnDefinition = "TEXT")
    private String tratamiento;

    @Column(columnDefinition = "TEXT")
    private String notas;

    // Relación con Cita
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cita_id", nullable = false, unique = true)
    private Cita cita;

    // Relación con Paciente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;
}