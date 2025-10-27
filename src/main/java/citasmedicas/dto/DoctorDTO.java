package citasmedicas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorDTO {

    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío.")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres.")
    private String nombre;

    @NotBlank(message = "La especialidad no puede estar vacía.")
    private String especialidad;

    @NotBlank(message = "El correo no puede estar vacío.")
    @Email(message = "El formato del correo no es válido.")
    private String correo;

    @Size(min = 8, max = 15, message = "El teléfono debe tener entre 8 y 15 caracteres.")
    private String telefono;
}