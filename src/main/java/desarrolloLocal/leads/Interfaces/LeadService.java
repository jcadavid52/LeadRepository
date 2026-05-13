package desarrolloLocal.leads.Interfaces;

import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.*;

public interface LeadService {
    LeadResponseGetAllDto GetAllAsync(LeadQueryGetAllDto query);
    LeadDto GetByIdAsync(String id);
    StatsResponseDto GetStats();
    String CreateAsync(LeadRequestCreateDto requestCreateDto);
    String GenerateSummaryAi(LeadRequestSummaryAI requestSummaryAI);
    void UpdateAsync(String id, LeadRequestPatchDto requestPatchDto);
    void DeleteAsync(String id);
}
