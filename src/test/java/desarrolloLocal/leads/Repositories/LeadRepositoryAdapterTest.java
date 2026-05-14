package desarrolloLocal.leads.Repositories;

import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.CountGroupFontResultDto;
import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.LeadQueryGetAllDto;
import desarrolloLocal.leads.Models.Entities.Lead;
import desarrolloLocal.leads.Models.Enums.FontType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeadRepositoryAdapterTest {

    @Mock
    private JpaLeadRepository jpaLeadRepository;

    @InjectMocks
    private LeadRepositoryAdapter adapter;

    private Lead createLead(String id, String name, FontType font) {
        Lead lead = new Lead();
        lead.setId(id);
        lead.setName(name);
        lead.setEmail(name + "@test.com");
        lead.setPhoneNumber("123456789");
        lead.setInterestingProduct("Product");
        lead.setFont(font);
        return lead;
    }

    @Test
    @DisplayName("GetAllAsync - pagina 0 por defecto devuelve primeros 10")
    void getAllAsync_defaultPage_returnsFirst10() {
        var query = new LeadQueryGetAllDto(null, null, null, null, null);
        List<Lead> allLeads = IntStream.range(0, 25)
                .mapToObj(i -> createLead("id" + i, "Lead " + i, FontType.other))
                .toList();

        when(jpaLeadRepository.findWithFilters(isNull(), isNull(), isNull())).thenReturn(allLeads);

        List<Lead> result = adapter.GetAllAsync(query);

        assertThat(result).hasSize(10);
        assertThat(result.get(0).getName()).isEqualTo("Lead 0");
        assertThat(result.get(9).getName()).isEqualTo("Lead 9");
        verify(jpaLeadRepository).findWithFilters(isNull(), isNull(), isNull());
    }

    @Test
    @DisplayName("GetAllAsync - pagina 1 devuelve siguientes 10")
    void getAllAsync_secondPage_returnsNext10() {
        var query = new LeadQueryGetAllDto(10, 1, null, null, null);
        List<Lead> allLeads = IntStream.range(0, 25)
                .mapToObj(i -> createLead("id" + i, "Lead " + i, FontType.other))
                .toList();

        when(jpaLeadRepository.findWithFilters(isNull(), isNull(), isNull())).thenReturn(allLeads);

        List<Lead> result = adapter.GetAllAsync(query);

        assertThat(result).hasSize(10);
        assertThat(result.get(0).getName()).isEqualTo("Lead 10");
        assertThat(result.get(9).getName()).isEqualTo("Lead 19");
    }

    @Test
    @DisplayName("GetAllAsync - ultima pagina devuelve los restantes")
    void getAllAsync_lastPage_returnsRemaining() {
        var query = new LeadQueryGetAllDto(10, 2, null, null, null);
        List<Lead> allLeads = IntStream.range(0, 25)
                .mapToObj(i -> createLead("id" + i, "Lead " + i, FontType.other))
                .toList();

        when(jpaLeadRepository.findWithFilters(isNull(), isNull(), isNull())).thenReturn(allLeads);

        List<Lead> result = adapter.GetAllAsync(query);

        assertThat(result).hasSize(5);
        assertThat(result.get(0).getName()).isEqualTo("Lead 20");
        assertThat(result.get(4).getName()).isEqualTo("Lead 24");
    }

    @Test
    @DisplayName("GetAllAsync - pagina fuera de rango devuelve lista vacia")
    void getAllAsync_pageOutOfBounds_returnsEmpty() {
        var query = new LeadQueryGetAllDto(10, 3, null, null, null);
        List<Lead> allLeads = IntStream.range(0, 25)
                .mapToObj(i -> createLead("id" + i, "Lead " + i, FontType.other))
                .toList();

        when(jpaLeadRepository.findWithFilters(isNull(), isNull(), isNull())).thenReturn(allLeads);

        List<Lead> result = adapter.GetAllAsync(query);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("GetAllAsync - lista vacia devuelve vacio")
    void getAllAsync_emptyList_returnsEmpty() {
        var query = new LeadQueryGetAllDto(null, null, null, null, null);

        when(jpaLeadRepository.findWithFilters(isNull(), isNull(), isNull())).thenReturn(List.of());

        List<Lead> result = adapter.GetAllAsync(query);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("GetAllAsync - con font filter lo pasa al repositorio")
    void getAllAsync_withFontFilter_passesToRepo() {
        var query = new LeadQueryGetAllDto(null, null, FontType.instagram, null, null);

        when(jpaLeadRepository.findWithFilters(eq(FontType.instagram), isNull(), isNull())).thenReturn(List.of());

        adapter.GetAllAsync(query);

        verify(jpaLeadRepository).findWithFilters(eq(FontType.instagram), isNull(), isNull());
    }

    @Test
    @DisplayName("GetAllAsync - con fechas convierte correctamente a LocalDateTime")
    void getAllAsync_withDateFilters_convertsCorrectly() {
        LocalDate startDate = LocalDate.of(2024, 1, 15);
        LocalDate finishDate = LocalDate.of(2024, 1, 20);
        var query = new LeadQueryGetAllDto(null, null, null, startDate, finishDate);

        when(jpaLeadRepository.findWithFilters(any(), any(), any())).thenReturn(List.of());

        adapter.GetAllAsync(query);

        verify(jpaLeadRepository).findWithFilters(
                isNull(),
                eq(startDate.atStartOfDay()),
                eq(finishDate.atTime(LocalTime.MAX))
        );
    }

    @Test
    @DisplayName("GetAllAsync - con solo startDate envia finishDate como null")
    void getAllAsync_withOnlyStartDate_sendsFinishNull() {
        LocalDate startDate = LocalDate.of(2024, 6, 1);
        var query = new LeadQueryGetAllDto(null, null, null, startDate, null);

        when(jpaLeadRepository.findWithFilters(any(), any(), any())).thenReturn(List.of());

        adapter.GetAllAsync(query);

        verify(jpaLeadRepository).findWithFilters(
                isNull(),
                eq(startDate.atStartOfDay()),
                isNull()
        );
    }

    @Test
    @DisplayName("GetByIdAsync - existe devuelve Optional con lead")
    void getByIdAsync_whenExists_returnsLead() {
        Lead lead = createLead("1", "Lead 1", FontType.other);
        when(jpaLeadRepository.findById("1")).thenReturn(Optional.of(lead));

        Optional<Lead> result = adapter.GetByIdAsync("1");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("1");
        verify(jpaLeadRepository).findById("1");
    }

    @Test
    @DisplayName("GetByIdAsync - no existe devuelve Optional vacio")
    void getByIdAsync_whenNotExists_returnsEmpty() {
        when(jpaLeadRepository.findById("999")).thenReturn(Optional.empty());

        Optional<Lead> result = adapter.GetByIdAsync("999");

        assertThat(result).isEmpty();
        verify(jpaLeadRepository).findById("999");
    }

    @Test
    @DisplayName("GetCountGroupFont - delega al repositorio JPA")
    void getCountGroupFont_delegatesToJpa() {
        var expected = List.of(new CountGroupFontResultDto(FontType.other, 5));
        when(jpaLeadRepository.getCountGroupFont()).thenReturn(expected);

        var result = adapter.GetCountGroupFont();

        assertThat(result).isSameAs(expected);
        verify(jpaLeadRepository).getCountGroupFont();
    }

    @Test
    @DisplayName("GetRecents - usa PageRequest.of(0, 7)")
    void getRecents_usesPageSize7() {
        LocalDateTime limitDate = LocalDateTime.now();
        var expectedLeads = List.of(createLead("1", "Recent", FontType.instagram));

        when(jpaLeadRepository.getRecents(limitDate, PageRequest.of(0, 7))).thenReturn(expectedLeads);

        var result = adapter.GetRecents(limitDate);

        assertThat(result).isSameAs(expectedLeads);
        verify(jpaLeadRepository).getRecents(limitDate, PageRequest.of(0, 7));
    }

    @Test
    @DisplayName("Count - retorna total de leads")
    void count_returnsTotal() {
        when(jpaLeadRepository.count()).thenReturn(42L);

        long result = adapter.Count();

        assertThat(result).isEqualTo(42L);
        verify(jpaLeadRepository).count();
    }

    @Test
    @DisplayName("CreateAsync - guarda y retorna el ID generado")
    void createAsync_savesAndReturnsId() {
        Lead lead = createLead(null, "New Lead", FontType.other);
        Lead savedLead = createLead("generated-uuid", "New Lead", FontType.other);

        when(jpaLeadRepository.save(lead)).thenReturn(savedLead);

        String result = adapter.CreateAsync(lead);

        assertThat(result).isEqualTo("generated-uuid");
        verify(jpaLeadRepository).save(lead);
    }

    @Test
    @DisplayName("UpdateAsync - delega save al repositorio JPA")
    void updateAsync_savesLead() {
        Lead lead = createLead("1", "Updated Lead", FontType.facebook);

        adapter.UpdateAsync(lead);

        verify(jpaLeadRepository).save(lead);
    }

    @Test
    @DisplayName("DeleteAsync - delega deleteById al repositorio JPA")
    void deleteAsync_deletesById() {
        adapter.DeleteAsync("some-id");

        verify(jpaLeadRepository).deleteById("some-id");
    }
}
