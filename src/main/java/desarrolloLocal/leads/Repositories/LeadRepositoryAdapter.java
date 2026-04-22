package desarrolloLocal.leads.Repositories;

import desarrolloLocal.leads.Interfaces.LeadRepository;
import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.LeadQueryGetAllDto;
import desarrolloLocal.leads.Models.Entities.Lead;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public class LeadRepositoryAdapter implements LeadRepository {

    @Autowired
    private JpaLeadRepository jpaLeadRepository;

    @Override
    public List<Lead> GetAllAsync(LeadQueryGetAllDto query) {
        var leads = GetData(query);
        return HandlePagination(leads, query.pageSize(), query.pageNumber());
    }

    @Override
    public Optional<Lead> GetByIdAsync(String id) {
        return jpaLeadRepository.findById(id);
    }

    @Override
    public String CreateAsync(Lead lead) {
        var leadCreated = jpaLeadRepository.save(lead);
        return leadCreated.getId();
    }

    @Override
    public void UpdateAsync(Lead lead) {
        jpaLeadRepository.save(lead);
    }

    private List<Lead> GetData(LeadQueryGetAllDto query) {
        LocalDateTime startDateTime = (query.startDate() != null)
                ? query.startDate().atStartOfDay()
                : null;

        LocalDateTime finishDateTime = (query.finishDate() != null)
                ? query.finishDate().atTime(LocalTime.MAX)
                : null;

        return jpaLeadRepository.findWithFilters(
                query.font(),
                startDateTime,
                finishDateTime
        );
    }

    private List<Lead> HandlePagination(List<Lead> leads, int pageSize, int pageNumber) {
        int start = pageNumber * pageSize;
        int end = Math.min(start + pageSize, leads.size());

        if (start >= leads.size()) {
            return List.of();
        }

        return leads.subList(start, end);
    }
}