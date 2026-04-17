package desarrolloLocal.leads.Repositories;

import desarrolloLocal.leads.Interfaces.LeadRepository;
import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.LeadQueryGetAllDto;
import desarrolloLocal.leads.Models.Entities.Lead;
import desarrolloLocal.leads.Models.Enums.FontType;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Repository
public class LeadRepositoryAdapter implements LeadRepository {

    @Override
    public List<Lead> GetAllAsync(LeadQueryGetAllDto query) {
        var leads = GetData(query);
        return HandlePagination(leads,query.pageSize(),query.pageNumber());
    }

    private List<Lead> GenerateSeedData(){
        List<Lead> leads = new ArrayList<>();

        var lead1 = new Lead();
        lead1.setId(UUID.randomUUID().toString());
        lead1.setName("lead name 1");
        lead1.setEmail("lead email 1");
        lead1.setPhoneNumber("lead phoneNumber 1");
        lead1.setInterestingProduct("lead interestingProduct 1");
        lead1.setFont(FontType.instagram);
        lead1.setCreationDate(LocalDateTime.of(2026,1,1,12,41,35));

        var lead2 = new Lead();
        lead2.setId(UUID.randomUUID().toString());
        lead2.setName("lead name 2");
        lead2.setEmail("lead email 2");
        lead2.setPhoneNumber("lead phoneNumber 2");
        lead2.setInterestingProduct("lead interestingProduct 2");
        lead2.setFont(FontType.facebook);
        lead2.setCreationDate(LocalDateTime.of(2026,2,3,12,41,35));

        var lead3 = new Lead();
        lead3.setId(UUID.randomUUID().toString());
        lead3.setName("lead name 3");
        lead3.setEmail("lead email 3");
        lead3.setPhoneNumber("lead phoneNumber 3");
        lead3.setInterestingProduct("lead interestingProduct 3");
        lead3.setFont(FontType.facebook);
        lead3.setCreationDate(LocalDateTime.of(2026,3,15,12,41,35));

        var lead4 = new Lead();
        lead4.setId(UUID.randomUUID().toString());
        lead4.setName("lead name 4");
        lead4.setEmail("lead email 4");
        lead4.setPhoneNumber("lead phoneNumber 4");
        lead4.setInterestingProduct("lead interestingProduct 4");
        lead4.setFont(FontType.landing_page);
        lead4.setCreationDate(LocalDateTime.of(2026,4,25,12,41,35));

        leads.add(lead1);
        leads.add(lead2);
        leads.add(lead3);
        leads.add(lead4);

        return leads;
    }

    private List<Lead> GetData(LeadQueryGetAllDto query){

        var leads = GenerateSeedData().stream()
                .sorted(Comparator.comparing(Lead::getCreationDate).reversed()).toList();

        final LocalDateTime startThreshold = (query.startDate() != null)
                ? query.startDate().atStartOfDay()
                : null;

        final LocalDateTime finishThreshold = (query.finishDate() != null)
                ? query.finishDate().atTime(LocalTime.MAX)
                : null;

        return leads.stream()

                .filter(l -> query.font() == null || Objects.equals(l.getFont(), query.font()))

                .filter(l -> startThreshold == null || !l.getCreationDate().isBefore(startThreshold))
                .filter(l -> finishThreshold == null || !l.getCreationDate().isAfter(finishThreshold))
                .toList();
    }

    private List<Lead> HandlePagination(List<Lead> leads,int pageSize, int pageNumber){
        int start = pageNumber * pageSize;
        int end = Math.min(start + pageSize, leads.size());

        return leads.subList(start,end);
    }
}
