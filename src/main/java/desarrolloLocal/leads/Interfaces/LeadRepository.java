package desarrolloLocal.leads.Interfaces;

import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.LeadQueryGetAllDto;
import desarrolloLocal.leads.Models.Entities.Lead;

import java.util.List;

public interface LeadRepository {
    List<Lead> GetAllAsync(LeadQueryGetAllDto query);
    String CreateAsync(Lead lead);
}
