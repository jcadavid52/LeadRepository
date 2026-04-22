package desarrolloLocal.leads.Mappers;

import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.LeadDto;
import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.LeadRequestCreateDto;
import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.LeadRequestPatchDto;
import desarrolloLocal.leads.Models.Entities.Lead;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LeadMapper {
    LeadDto leadToDto(Lead lead);
    List<LeadDto> leadToListDto(List<Lead> leads);
    Lead createRequestToEntity(LeadRequestCreateDto request);
    void updateLeadFromPatch(LeadRequestPatchDto request, @MappingTarget Lead lead);
}
