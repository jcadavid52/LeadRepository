package desarrolloLocal.leads.Models.Dtos.LeadModelDtos;

import desarrolloLocal.leads.Models.Enums.FontType;

public record CountGroupFontResultDto(
        FontType font,
        long countByFont) {
}
