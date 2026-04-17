package desarrolloLocal.leads.Models.Dtos.LeadModelDtos;

import java.util.List;

public record LeadResponseGetAllDto(List<LeadDto> leads,int count) {
}
