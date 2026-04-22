package desarrolloLocal.leads.Models.Dtos.LeadModelDtos;

import desarrolloLocal.leads.Models.Enums.FontType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LeadRequestPatchDto(
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        String name,
        @Email(message = "El formato del email no es válido")
        String email,
        @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "El teléfono debe contener entre 7 y 15 dígitos y puede incluir el prefijo '+'")
        String phoneNumber,
        FontType font,
        @Size(max = 255, message = "La descripción del producto es demasiado larga")
        String interestingProduct
) { }
