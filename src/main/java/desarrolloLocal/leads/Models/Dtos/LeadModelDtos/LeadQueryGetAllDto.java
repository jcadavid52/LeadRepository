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
}
