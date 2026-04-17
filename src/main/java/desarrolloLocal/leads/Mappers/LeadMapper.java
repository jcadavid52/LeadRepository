package desarrolloLocal.leads.Mappers;

import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.LeadDto;
import desarrolloLocal.leads.Models.Entities.Lead;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LeadMapper {
    LeadDto map(Lead lead);
    List<LeadDto> leadToListDto(List<Lead> leads);
}
