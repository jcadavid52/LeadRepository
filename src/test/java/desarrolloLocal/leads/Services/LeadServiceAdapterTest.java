package desarrolloLocal.leads.Services;

import com.google.genai.Client;
import com.google.genai.Models;
import com.google.genai.types.GenerateContentResponse;
import desarrolloLocal.leads.Exceptions.ResourceNotFoundException;
import desarrolloLocal.leads.Interfaces.LeadRepository;
import desarrolloLocal.leads.Mappers.LeadMapper;
import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.*;
import desarrolloLocal.leads.Models.Entities.Lead;
import desarrolloLocal.leads.Models.Enums.FontType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeadServiceAdapterTest {

    @Mock
    private LeadRepository leadRepository;

    @Mock
    private LeadMapper leadMapper;

    @Mock
    private Client client;

    @Mock
    private Models models;

    @Mock
    private GenerateContentResponse generateContentResponse;

    @InjectMocks
    private LeadServiceAdapter leadServiceAdapter;

    @BeforeEach
    void setUp() throws Exception {
        Field modelsField = Client.class.getDeclaredField("models");
        modelsField.setAccessible(true);
        modelsField.set(client, models);
    }

    private Lead createLead(String id, String name) {
        Lead lead = new Lead();
        lead.setId(id);
        lead.setName(name);
        lead.setEmail("email@correo.com");
        lead.setPhoneNumber("123456789");
        lead.setInterestingProduct("producto");
        lead.setFont(FontType.other);
        lead.setCreationDate(LocalDateTime.now());
        return lead;
    }

    private LeadDto createLeadDto(String id, String name) {
        return new LeadDto(
                id, name, "email@correo.com", "123456789",
                "producto", FontType.other,
                LocalDateTime.now(), null
        );
    }

    @Test
    @DisplayName("GetAllAsync - debe retornar lista paginada con count")
    void getAllAsync_shouldReturnResponseWithLeads() {
        var query = new LeadQueryGetAllDto(null, null, null, null, null);
        List<Lead> leads = List.of(createLead("1", "Lead 1"), createLead("2", "Lead 2"));
        List<LeadDto> dtos = List.of(createLeadDto("1", "Lead 1"), createLeadDto("2", "Lead 2"));

        when(leadRepository.GetAllAsync(query)).thenReturn(leads);
        when(leadMapper.leadToListDto(leads)).thenReturn(dtos);

        var result = leadServiceAdapter.GetAllAsync(query);

        assertThat(result.leads()).hasSize(2);
        assertThat(result.count()).isEqualTo(2);
        verify(leadRepository).GetAllAsync(query);
        verify(leadMapper).leadToListDto(leads);
    }

    @Test
    @DisplayName("GetAllAsync - debe retornar lista vacia cuando no hay leads")
    void getAllAsync_shouldReturnEmptyResponse_whenNoLeads() {
        var query = new LeadQueryGetAllDto(null, null, null, null, null);
        when(leadRepository.GetAllAsync(query)).thenReturn(List.of());
        when(leadMapper.leadToListDto(List.of())).thenReturn(List.of());

        var result = leadServiceAdapter.GetAllAsync(query);

        assertThat(result.leads()).isEmpty();
        assertThat(result.count()).isZero();
    }

    @Test
    @DisplayName("GetByIdAsync - debe retornar LeadDto cuando existe")
    void getByIdAsync_shouldReturnDto_whenExists() {
        Lead lead = createLead("1", "Lead 1");
        LeadDto dto = createLeadDto("1", "Lead 1");

        when(leadRepository.GetByIdAsync("1")).thenReturn(Optional.of(lead));
        when(leadMapper.leadToDto(lead)).thenReturn(dto);

        var result = leadServiceAdapter.GetByIdAsync("1");

        assertThat(result.id()).isEqualTo("1");
        assertThat(result.name()).isEqualTo("Lead 1");
        verify(leadRepository).GetByIdAsync("1");
        verify(leadMapper).leadToDto(lead);
    }

    @Test
    @DisplayName("GetByIdAsync - debe lanzar ResourceNotFoundException cuando no existe")
    void getByIdAsync_shouldThrow_whenNotFound() {
        when(leadRepository.GetByIdAsync("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> leadServiceAdapter.GetByIdAsync("999"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");

        verify(leadRepository).GetByIdAsync("999");
        verifyNoInteractions(leadMapper);
    }

    @Test
    @DisplayName("GetStats - debe retornar estadisticas completas")
    void getStats_shouldReturnStats() {
        List<Lead> recents = List.of(createLead("1", "Recent Lead"));
        List<LeadDto> recentsDtos = List.of(createLeadDto("1", "Recent Lead"));
        List<CountGroupFontResultDto> groupFont = List.of(
                new CountGroupFontResultDto(FontType.other, 5)
        );

        when(leadRepository.Count()).thenReturn(10L);
        when(leadRepository.GetRecents(any())).thenReturn(recents);
        when(leadMapper.leadToListDto(recents)).thenReturn(recentsDtos);
        when(leadRepository.GetCountGroupFont()).thenReturn(groupFont);

        var result = leadServiceAdapter.GetStats();

        assertThat(result.totalLeads()).isEqualTo(10);
        assertThat(result.recentsLeads()).hasSize(1);
        assertThat(result.groupFont()).hasSize(1);
        verify(leadRepository).Count();
        verify(leadRepository).GetRecents(any());
        verify(leadRepository).GetCountGroupFont();
    }

    @Test
    @DisplayName("CreateAsync - debe crear lead y retornar el ID generado")
    void createAsync_shouldReturnNewId() {
        var request = new LeadRequestCreateDto(
                "New Lead", "new@correo.com", "+123456789",
                FontType.instagram, "new product"
        );
        Lead lead = createLead(null, "New Lead");

        when(leadMapper.createRequestToEntity(request)).thenReturn(lead);
        when(leadRepository.CreateAsync(lead)).thenReturn("new-uuid-123");

        var result = leadServiceAdapter.CreateAsync(request);

        assertThat(result).isEqualTo("new-uuid-123");
        verify(leadMapper).createRequestToEntity(request);
        verify(leadRepository).CreateAsync(lead);
    }

    @Test
    @DisplayName("GenerateSummaryAi - debe generar resumen IA cuando hay mas de 1 lead")
    void generateSummaryAi_shouldReturnAiSummary_whenMultipleLeads() {
        var query = new LeadQueryGetAllDto(null, null, null, null, null);
        var request = new LeadRequestSummaryAI(query, "gemini-2.0-flash");

        List<Lead> leads = List.of(
                createLead("1", "Lead 1"),
                createLead("2", "Lead 2")
        );

        when(leadRepository.GetAllAsync(query)).thenReturn(leads);
        when(generateContentResponse.text()).thenReturn("Resumen ejecutivo generado por IA");
        when(models.generateContent(eq("gemini-2.0-flash"), anyString(), any()))
                .thenReturn(generateContentResponse);

        var result = leadServiceAdapter.GenerateSummaryAi(request);

        assertThat(result).isEqualTo("Resumen ejecutivo generado por IA");
        verify(leadRepository).GetAllAsync(query);
        verify(models).generateContent(eq("gemini-2.0-flash"), anyString(), any());
    }

    @Test
    @DisplayName("GenerateSummaryAi - debe retornar mensaje por defecto cuando hay 0 o 1 lead")
    void generateSummaryAi_shouldReturnDefaultMessage_whenOneOrZeroLeads() {
        var query = new LeadQueryGetAllDto(null, null, null, null, null);
        var request = new LeadRequestSummaryAI(query, "gemini-2.0-flash");

        when(leadRepository.GetAllAsync(query)).thenReturn(List.of(createLead("1", "Solo Lead")));

        var result = leadServiceAdapter.GenerateSummaryAi(request);

        assertThat(result).contains("Los filtros ingresados no generan una data adecuada");
        verify(leadRepository).GetAllAsync(query);
        verifyNoInteractions(models);
    }

    @Test
    @DisplayName("UpdateAsync - debe actualizar lead cuando existe")
    void updateAsync_shouldUpdate_whenExists() {
        Lead existingLead = createLead("1", "Original Name");
        var patch = new LeadRequestPatchDto("Updated Name", null, null, null, null);

        when(leadRepository.GetByIdAsync("1")).thenReturn(Optional.of(existingLead));

        leadServiceAdapter.UpdateAsync("1", patch);

        verify(leadMapper).updateLeadFromPatch(patch, existingLead);
        assertThat(existingLead.getUpdateDate()).isNotNull();
        verify(leadRepository).UpdateAsync(existingLead);
    }

    @Test
    @DisplayName("UpdateAsync - debe lanzar ResourceNotFoundException cuando no existe")
    void updateAsync_shouldThrow_whenNotFound() {
        var patch = new LeadRequestPatchDto("Updated Name", null, null, null, null);

        when(leadRepository.GetByIdAsync("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> leadServiceAdapter.UpdateAsync("999", patch))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");

        verify(leadRepository).GetByIdAsync("999");
        verify(leadRepository, never()).UpdateAsync(any());
        verifyNoInteractions(leadMapper);
    }

    @Test
    @DisplayName("DeleteAsync - debe eliminar lead cuando existe")
    void deleteAsync_shouldDelete_whenExists() {
        Lead existingLead = createLead("1", "Lead to delete");

        when(leadRepository.GetByIdAsync("1")).thenReturn(Optional.of(existingLead));

        leadServiceAdapter.DeleteAsync("1");

        verify(leadRepository).GetByIdAsync("1");
        verify(leadRepository).DeleteAsync("1");
    }

    @Test
    @DisplayName("DeleteAsync - debe lanzar ResourceNotFoundException cuando no existe")
    void deleteAsync_shouldThrow_whenNotFound() {
        when(leadRepository.GetByIdAsync("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> leadServiceAdapter.DeleteAsync("999"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");

        verify(leadRepository).GetByIdAsync("999");
        verify(leadRepository, never()).DeleteAsync(any());
    }
}
