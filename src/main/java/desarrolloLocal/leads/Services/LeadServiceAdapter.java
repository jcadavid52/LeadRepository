package desarrolloLocal.leads.Services;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import desarrolloLocal.leads.Exceptions.ResourceNotFoundException;
import desarrolloLocal.leads.Interfaces.LeadRepository;
import desarrolloLocal.leads.Interfaces.LeadService;
import desarrolloLocal.leads.Mappers.LeadMapper;
import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.*;
import desarrolloLocal.leads.Models.Entities.Lead;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeadServiceAdapter implements LeadService {

    private final LeadRepository leadRepository;
    private final LeadMapper leadMapper;
    private final Client client;

    public LeadServiceAdapter(
            LeadRepository leadRepository,
            LeadMapper leadMapper, Client client
    ) {
        this.leadRepository = leadRepository;
        this.leadMapper = leadMapper;
        this.client = client;
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
    public StatsResponseDto GetStats() {
        int quantityDays = 7;
        var daysAgo = LocalDateTime.now().minusDays(quantityDays);
        var leadRecents = leadRepository.GetRecents(daysAgo);
        var leadRecentsDtos = leadMapper.leadToListDto(leadRecents);

        long totalLeads = leadRepository.Count();

        var groupFont = leadRepository.GetCountGroupFont();

        return new StatsResponseDto(totalLeads,groupFont,leadRecentsDtos);
    }

    @Override
    public String CreateAsync(LeadRequestCreateDto requestCreateDto) {
        Lead leadToCreate = leadMapper.createRequestToEntity(requestCreateDto);
        return leadRepository.CreateAsync(leadToCreate);
    }

    @Override
    public String GenerateSummaryAi(LeadRequestSummaryAI requestSummaryAI) {

        var leads = leadRepository.GetAllAsync(requestSummaryAI.query());

        String leadsString = leads.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));

        String prompt = "genera un resumen ejecutivo: analisis general, fuente principal, recomendaciones de esta lista de leads: " + leadsString;

        var response = client.models.generateContent(
                requestSummaryAI.modelIA(),
                prompt,
                null);

        return response.text();
    }

    @Override
    public void UpdateAsync(String id, LeadRequestPatchDto requestPatchDto) {
        Lead existingLead = leadRepository.GetByIdAsync(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead no encontrado con id: " + id));

        leadMapper.updateLeadFromPatch(requestPatchDto,existingLead);
        existingLead.setUpdateDate(LocalDateTime.now());
        leadRepository.UpdateAsync(existingLead);
    }

    @Override
    public void DeleteAsync(String id) {
        Lead existingLead = leadRepository.GetByIdAsync(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead no encontrado con id: " + id));
        leadRepository.DeleteAsync(id);
    }
}
