package desarrolloLocal.leads.Controllers;

import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.LeadQueryGetAllDto;
import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.LeadResponseGetAllDto;
import desarrolloLocal.leads.Models.Enums.FontType;
import desarrolloLocal.leads.Services.LeadServiceAdapter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            @RequestParam(required = false)LocalDate finishDate,
            @RequestParam(required = false)FontType font,
            @RequestParam(defaultValue = "5")int pageSize,
            @RequestParam(defaultValue = "0")int pageNumber
            ){

        var query = new LeadQueryGetAllDto(
            pageSize,
            pageNumber,
            font,
            startDate,
            finishDate
        );

        var leads = leadServiceAdapter.GetAllAsync(query);
        return ResponseEntity.ok(leads);
    }
}
