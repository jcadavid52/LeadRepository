package desarrolloLocal.leads.Repositories;

import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.CountGroupFontResultDto;
import desarrolloLocal.leads.Models.Entities.Lead;
import desarrolloLocal.leads.Models.Enums.FontType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class JpaLeadRepositoryTest {

    @Autowired
    private JpaLeadRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        FontType[] fonts = {FontType.instagram, FontType.facebook, FontType.landing_page, FontType.referred, FontType.other};
        for (int i = 0; i < fonts.length; i++) {
            Lead lead = new Lead();
            lead.setName("Lead " + i);
            lead.setEmail("lead" + i + "@test.com");
            lead.setPhoneNumber("123456789");
            lead.setInterestingProduct("Product " + i);
            lead.setFont(fonts[i]);
            repository.save(lead);
        }
    }

    @Test
    @DisplayName("findWithFilters - sin filtros devuelve todos")
    void findWithFilters_noFilters_returnsAll() {
        List<Lead> result = repository.findWithFilters(null, null, null);
        assertThat(result).hasSize(5);
    }

    @Test
    @DisplayName("findWithFilters - filtrando por font devuelve solo la font indicada")
    void findWithFilters_byFont_returnsFiltered() {
        List<Lead> result = repository.findWithFilters(FontType.instagram, null, null);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFont()).isEqualTo(FontType.instagram);
    }

    @Test
    @DisplayName("findWithFilters - filtro por fecha dentro del rango devuelve leads")
    void findWithFilters_byDateRange_returnsLeads() {
        List<Lead> result = repository.findWithFilters(
                null,
                LocalDateTime.now().minusDays(365),
                LocalDateTime.now().plusDays(365)
        );
        assertThat(result).hasSize(5);
    }

    @Test
    @DisplayName("findWithFilters - filtro por fecha fuera del rango devuelve vacio")
    void findWithFilters_byDateRangeNoMatch_returnsEmpty() {
        List<Lead> result = repository.findWithFilters(
                null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(365)
        );
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findWithFilters - filtrando por font + fecha devuelve solo coincidencias")
    void findWithFilters_byFontAndDateRange_returnsFiltered() {
        List<Lead> result = repository.findWithFilters(
                FontType.facebook,
                LocalDateTime.now().minusDays(365),
                LocalDateTime.now().plusDays(365)
        );
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFont()).isEqualTo(FontType.facebook);
    }

    @Test
    @DisplayName("getCountGroupFont - agrupa por font y suma total correcta")
    void getCountGroupFont_returnsGroupedCounts() {
        List<CountGroupFontResultDto> result = repository.getCountGroupFont();
        assertThat(result).hasSize(5);
        long total = result.stream().mapToLong(CountGroupFontResultDto::countByFont).sum();
        assertThat(total).isEqualTo(5);
    }

    @Test
    @DisplayName("getRecents - dentro del rango devuelve leads recientes")
    void getRecents_withinRange_returnsLeads() {
        List<Lead> result = repository.getRecents(LocalDateTime.now().minusDays(1), PageRequest.of(0, 10));
        assertThat(result).hasSize(5);
    }

    @Test
    @DisplayName("getRecents - fuera del rango devuelve vacio")
    void getRecents_outOfRange_returnsEmpty() {
        List<Lead> result = repository.getRecents(LocalDateTime.now().plusDays(1), PageRequest.of(0, 10));
        assertThat(result).isEmpty();
    }
}
