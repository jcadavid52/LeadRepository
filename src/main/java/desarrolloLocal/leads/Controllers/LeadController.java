package desarrolloLocal.leads.Controllers;

import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.*;
import desarrolloLocal.leads.Models.Enums.FontType;
import desarrolloLocal.leads.Services.LeadServiceAdapter;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

@RestController
@RequestMapping("api/leads")
public class LeadController {
    private final LeadServiceAdapter leadServiceAdapter;

    public LeadController(LeadServiceAdapter leadServiceAdapter) {
        this.leadServiceAdapter = leadServiceAdapter;
    }

    @GetMapping
    public ResponseEntity<LeadResponseGetAllDto> Get(LeadQueryGetAllDto query) {
        var leads = leadServiceAdapter.GetAllAsync(query);
        return ResponseEntity.ok(leads);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeadDto> GetById(@PathVariable String id) {
        LeadDto lead = leadServiceAdapter.GetByIdAsync(id);
        return ResponseEntity.ok(lead);
    }

    @GetMapping("/stats")
    public ResponseEntity<StatsResponseDto> GetStats(){
        var stats = leadServiceAdapter.GetStats();

        return ResponseEntity.ok(stats);
    }

    @PostMapping
    public ResponseEntity<String> CreateAsync(@Valid @RequestBody LeadRequestCreateDto request){
        String id = leadServiceAdapter.CreateAsync(request);
        var location = URI.create("api/leads/" + id);

        return ResponseEntity.created(location).body("Lead creado con éxito: " + id);
    }

    @PostMapping("/ai/summary")
    public ResponseEntity<?> SummaryAI(@RequestBody LeadRequestSummaryAI requestSummaryAI){

        String responseSummaryAi = leadServiceAdapter.GenerateSummaryAi(requestSummaryAI);

        return ResponseEntity.ok(responseSummaryAi);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> UpdateAsync(
            @PathVariable String id,
           @Valid @RequestBody LeadRequestPatchDto request) {

        leadServiceAdapter.UpdateAsync(id, request);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> DeleteAsync(@PathVariable String id) {
        leadServiceAdapter.DeleteAsync(id);
        return ResponseEntity.noContent().build();
    }
}
