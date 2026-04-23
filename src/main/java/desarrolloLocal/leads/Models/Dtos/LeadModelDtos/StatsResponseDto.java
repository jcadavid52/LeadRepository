package desarrolloLocal.leads.Models.Dtos.LeadModelDtos;
import java.util.List;

public record StatsResponseDto(
        long totalLeads,
        List<CountGroupFontResultDto> groupFont,
        List<LeadDto> recentsLeads) {
}
