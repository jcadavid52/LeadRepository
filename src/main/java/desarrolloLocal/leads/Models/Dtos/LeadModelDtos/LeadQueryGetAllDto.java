package desarrolloLocal.leads.Models.Dtos.LeadModelDtos;

import desarrolloLocal.leads.Models.Enums.FontType;

import java.time.LocalDate;


public record LeadQueryGetAllDto(
        Integer pageSize,
        Integer pageNumber,
        FontType font,
        LocalDate startDate,
        LocalDate finishDate
){
    public LeadQueryGetAllDto {
        if (pageSize == null) pageSize = 10;
        if (pageNumber == null) pageNumber = 0;

        if (startDate != null && finishDate != null && startDate.isAfter(finishDate)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la de fin");
        }
    }
}
