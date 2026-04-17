package desarrolloLocal.leads.Models.Dtos.LeadModelDtos;

import desarrolloLocal.leads.Models.Enums.FontType;

import java.time.LocalDateTime;

public record LeadDto(
    String name,
    String email,
    String phoneNumber,
    String interestingProduct,
    FontType font,
    LocalDateTime creationDate
) {
}
