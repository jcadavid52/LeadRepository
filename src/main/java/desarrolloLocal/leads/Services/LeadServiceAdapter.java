package desarrolloLocal.leads.Services;

import desarrolloLocal.leads.Interfaces.LeadRepository;
import desarrolloLocal.leads.Interfaces.LeadService;
import desarrolloLocal.leads.Mappers.LeadMapper;
import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.LeadQueryGetAllDto;
import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.LeadRequestCreateDto;
import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.LeadResponseGetAllDto;
import desarrolloLocal.leads.Models.Entities.Lead;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        List<Lead> leads;

        leads = leadRepository.GetAllAsync(query);

        var leadsMappers = leadMapper.leadToListDto(leads);
        return new LeadResponseGetAllDto(leadsMappers,leadsMappers.size());
    }

    @Override
    public String CreateAsync(LeadRequestCreateDto requestCreateDto) {
        var leadToCreate = leadMapper.createRequestToEntity(requestCreateDto);
        return leadRepository.CreateAsync(leadToCreate);
    }
}
