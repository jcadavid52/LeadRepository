package desarrolloLocal.leads.Controllers;

import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.*;
import desarrolloLocal.leads.Models.Enums.FontType;
import desarrolloLocal.leads.Services.LeadServiceAdapter;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;

@RestController
@RequestMapping("api/leads")
public class LeadController {
    private final LeadServiceAdapter leadServiceAdapter;

    public LeadController(LeadServiceAdapter leadServiceAdapter) {
        this.leadServiceAdapter = leadServiceAdapter;
    }

    @GetMapping
    public ResponseEntity<LeadResponseGetAllDto> Get(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate finishDate,
            @RequestParam(required = false) FontType font,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "0") int pageNumber
    ) {
        var query = new LeadQueryGetAllDto(pageSize, pageNumber, font, startDate, finishDate);
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
