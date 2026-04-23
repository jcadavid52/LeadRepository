package desarrolloLocal.leads.Repositories;

import desarrolloLocal.leads.Models.Dtos.LeadModelDtos.CountGroupFontResultDto;
import desarrolloLocal.leads.Models.Entities.Lead;
import desarrolloLocal.leads.Models.Enums.FontType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JpaLeadRepository extends JpaRepository<Lead, String> {

    @Query("SELECT l FROM Lead l WHERE " +
           "(:font IS NULL OR l.font = :font) AND " +
           "(:startDate IS NULL OR l.creationDate >= :startDate) AND " +
           "(:finishDate IS NULL OR l.creationDate <= :finishDate) " +
           "ORDER BY l.creationDate DESC")
    List<Lead> findWithFilters(
        @Param("font") FontType font,
        @Param("startDate") LocalDateTime startDate,
        @Param("finishDate") LocalDateTime finishDate
    );

    @Query("SELECT l.font,COUNT(*) FROM Lead l GROUP BY l.font")
    List<CountGroupFontResultDto> getCountGroupFont();

    @Query("SELECT l FROM Lead l WHERE l.creationDate >= :limitDate ORDER BY l.creationDate DESC")
    List<Lead> getRecents(@Param("limitDate") LocalDateTime limitDate, Pageable pageable);
}