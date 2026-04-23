package desarrolloLocal.leads.Interfaces;

import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.CountGroupFontResultDto;
import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.LeadQueryGetAllDto;
import desarrolloLocal.leads.Models.Entities.Lead;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LeadRepository {
    List<Lead> GetAllAsync(LeadQueryGetAllDto query);
    Optional<Lead> GetByIdAsync(String id);
    List<CountGroupFontResultDto> GetCountGroupFont();
    List<Lead> GetRecents(LocalDateTime fechaLimite);
    long Count();
    String CreateAsync(Lead lead);
    void UpdateAsync(Lead lead);
    void DeleteAsync(String id);
}
