package desarrolloLocal.leads.Models.Dtos.LeadModelDtos;

import desarrolloLocal.leads.Models.Enums.FontType;
import jakarta.validation.constraints.*;

public record LeadRequestCreateDto(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        String name,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato del email no es válido")
        String email,

        @NotBlank(message = "El número de teléfono es obligatorio")
        @Pattern(regexp = "^\\+?[0-9]{7,10}$", message = "El teléfono debe contener entre 7 y 15 dígitos y puede incluir el prefijo '+'")
        String phoneNumber,

        @NotNull(message = "La fuente (font) es obligatoria")
        FontType font,

        @Size(max = 255, message = "La descripción del producto es demasiado larga")
        String interestingProduct
) {
}
