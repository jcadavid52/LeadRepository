package desarrolloLocal.leads.Mappers;

import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.LeadDto;
import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.LeadRequestCreateDto;
import desarrolloLocal.leads.Models.Entities.Lead;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LeadMapper {
    LeadDto leadToDto(Lead lead);
    List<LeadDto> leadToListDto(List<Lead> leads);
    Lead createRequestToEntity(LeadRequestCreateDto request);
}
