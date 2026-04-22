package desarrolloLocal.leads.Services;

import desarrolloLocal.leads.Exceptions.ResourceNotFoundException;
import desarrolloLocal.leads.Interfaces.LeadRepository;
import desarrolloLocal.leads.Interfaces.LeadService;
import desarrolloLocal.leads.Mappers.LeadMapper;
import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.LeadDto;
import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.LeadQueryGetAllDto;
import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.LeadRequestCreateDto;
import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.LeadResponseGetAllDto;
import desarrolloLocal.leads.Models.Entities.Lead;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeadServiceAdapter implements LeadService {

    private final LeadRepository leadRepository;
    private final LeadMapper leadMapper;

    public LeadServiceAdapter(
            LeadRepository leadRepository,
            LeadMapper leadMapper
    ) {
        this.leadRepository = leadRepository;
        this.leadMapper = leadMapper;
    }

    @Override
    public LeadResponseGetAllDto GetAllAsync(LeadQueryGetAllDto query){
        List<Lead> leads = leadRepository.GetAllAsync(query);
        var leadsMappers = leadMapper.leadToListDto(leads);
        return new LeadResponseGetAllDto(leadsMappers, leadsMappers.size());
    }

    @Override
    public LeadDto GetByIdAsync(String id) {
        Lead lead = leadRepository.GetByIdAsync(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead no encontrado con id: " + id));
        return leadMapper.leadToDto(lead);
    }

    @Override
    public String CreateAsync(LeadRequestCreateDto requestCreateDto) {
        Lead leadToCreate = leadMapper.createRequestToEntity(requestCreateDto);
        return leadRepository.CreateAsync(leadToCreate);
    }
}
