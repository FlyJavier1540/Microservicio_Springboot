package com.citasmedicas.api.services;

import com.citasmedicas.api.models.Cita;
import com.citasmedicas.api.repositories.CitaRepository; // <-- Importa esto
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <-- Importa esto

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final CitaRepository citaRepository; // <-- Inyecta el repositorio

    @Async
    @Transactional(readOnly = true) // <-- Añade la transacción
    public void notificarCambioDeEstadoCita(Long citaId) { // <-- Ahora recibimos solo el ID
        // Volvemos a cargar la cita dentro de la nueva transacción para tener todos los datos
        Cita cita = citaRepository.findById(citaId)
                .orElse(null);

        if (cita == null) {
            // No se pudo encontrar la cita, no se puede enviar el correo.
            // Aquí se podría registrar un log.
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        String to = cita.getPaciente().getUsuario().getEmail(); // Ahora esto funcionará
        String subject = "";
        String text = "";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy 'a las' HH:mm 'hrs.'");
        String fechaHoraFormateada = cita.getFechaHora().format(formatter);

        switch (cita.getEstado()) {
            case PROGRAMADA:
                subject = "¡Tu Cita ha sido Confirmada!";
                text = String.format(
                        "Hola %s,\n\nTu cita con el/la Dr(a). %s ha sido confirmada.\n\n" +
                                "Fecha y Hora: %s\nMotivo: %s\n\n" +
                                "¡Te esperamos!",
                        cita.getPaciente().getNombreCompleto(),
                        cita.getDoctor().getNombreCompleto(),
                        fechaHoraFormateada,
                        cita.getMotivoConsulta()
                );
                break;
            case RECHAZADA:
                subject = "Actualización sobre tu Solicitud de Cita";
                text = String.format(
                        "Hola %s,\n\nLamentamos informarte que tu solicitud de cita con el/la Dr(a). %s para la fecha %s no pudo ser aprobada en esta ocasión.\n\n" +
                                "Puedes intentar solicitar una nueva cita en otra fecha.\n\nGracias por tu comprensión.",
                        cita.getPaciente().getNombreCompleto(),
                        cita.getDoctor().getNombreCompleto(),
                        cita.getFechaHora().toLocalDate().toString()
                );
                break;
            default:
                return;
        }

        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }
}