package desarrolloLocal.leads.Interfaces;

import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.LeadDto;
import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.LeadQueryGetAllDto;
import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.LeadRequestCreateDto;
import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.LeadRequestPatchDto;
import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.LeadResponseGetAllDto;

public interface LeadService {
    LeadResponseGetAllDto GetAllAsync(LeadQueryGetAllDto query);
    LeadDto GetByIdAsync(String id);
    String CreateAsync(LeadRequestCreateDto requestCreateDto);
    void UpdateAsync(String id, LeadRequestPatchDto requestPatchDto);
}
