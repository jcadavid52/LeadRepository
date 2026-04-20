package desarrolloLocal.leads.Interfaces;

import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.LeadQueryGetAllDto;
import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.LeadRequestCreateDto;
import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.LeadResponseGetAllDto;

import java.util.List;

public interface LeadService {
    LeadResponseGetAllDto GetAllAsync(LeadQueryGetAllDto query);
    String CreateAsync(LeadRequestCreateDto requestCreateDto);
}
