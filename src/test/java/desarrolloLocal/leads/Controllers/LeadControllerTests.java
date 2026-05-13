package desarrolloLocal.leads.Controllers;

import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.LeadRequestCreateDto;
import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.LeadRequestPatchDto;
import desarrolloLocal.leads.Models.Entities.Lead;
import desarrolloLocal.leads.Models.Enums.FontType;
import desarrolloLocal.leads.Repositories.JpaLeadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LeadControllerTests {

    @LocalServerPort
    private int port;

    private WebTestClient webClient;

    @Autowired
    private JpaLeadRepository jpaLeadRepository;

    private String existingLeadId;

    @BeforeEach
    void setUp() {
        webClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();

        jpaLeadRepository.deleteAll();

        List<Lead> leads = IntStream.rangeClosed(1, 25)
                .mapToObj(i -> createLead(
                        "name " + i,
                        "email@correo" + i + ".com",
                        "producto " + i,
                        FontType.other
                ))
                .toList();

        jpaLeadRepository.saveAll(leads);
        existingLeadId = jpaLeadRepository.findAll().get(0).getId();
    }

    private Lead createLead(String name, String email, String product, FontType font) {
        Lead lead = new Lead();
        lead.setName(name);
        lead.setEmail(email);
        lead.setInterestingProduct(product);
        lead.setFont(font);
        lead.setPhoneNumber("123456789");
        return lead;
    }

    @Test
    @DisplayName("GET /api/leads - debe retornar 200 con lista paginada")
    void getAllOk() {
        webClient.get().uri("/api/leads")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.leads").isArray()
                .jsonPath("$.count").isNumber();
    }

    @Test
    @DisplayName("GET /api/leads/{id} - debe retornar 200 cuando existe")
    void getById_whenExists_returns200() {
        webClient.get().uri("/api/leads/{id}", existingLeadId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(existingLeadId)
                .jsonPath("$.name").isNotEmpty();
    }

    @Test
    @DisplayName("GET /api/leads/{id} - debe retornar 404 cuando no existe")
    void getById_whenNotExists_returns404() {
        webClient.get().uri("/api/leads/non-existent-id")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("GET /api/leads/stats - debe retornar 200 con estadisticas")
    void getStats_returns200() {
        webClient.get().uri("/api/leads/stats")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.totalLeads").isNumber()
                .jsonPath("$.groupFont").isArray()
                .jsonPath("$.recentsLeads").isArray();
    }

    @Test
    @DisplayName("POST /api/leads - debe crear y retornar 201")
    void createLead_returns201() {
        var request = new LeadRequestCreateDto(
                "Test Name",
                "test@example.com",
                "+1234567890",
                FontType.instagram,
                "Test Product"
        );

        webClient.post().uri("/api/leads")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("éxito"));
    }

    @Test
    @DisplayName("POST /api/leads - debe retornar 400 cuando los datos son invalidos")
    void createLead_withInvalidData_returns400() {
        var request = new LeadRequestCreateDto(
                "",
                "invalid-email",
                "abc",
                null,
                null
        );

        webClient.post().uri("/api/leads")
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("PATCH /api/leads/{id} - debe actualizar y retornar 204")
    void patchLead_whenExists_returns204() {
        var request = new LeadRequestPatchDto(
                "Updated Name",
                null,
                null,
                null,
                null
        );

        webClient.patch().uri("/api/leads/{id}", existingLeadId)
                .bodyValue(request)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("PATCH /api/leads/{id} - debe retornar 404 cuando no existe")
    void patchLead_whenNotExists_returns404() {
        var request = new LeadRequestPatchDto(
                "Updated Name",
                null,
                null,
                null,
                null
        );

        webClient.patch().uri("/api/leads/non-existent-id")
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("DELETE /api/leads/{id} - debe eliminar y retornar 204")
    void deleteLead_whenExists_returns204() {
        webClient.delete().uri("/api/leads/{id}", existingLeadId)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("DELETE /api/leads/{id} - debe retornar 404 cuando no existe")
    void deleteLead_whenNotExists_returns404() {
        webClient.delete().uri("/api/leads/non-existent-id")
                .exchange()
                .expectStatus().isNotFound();
    }
}
