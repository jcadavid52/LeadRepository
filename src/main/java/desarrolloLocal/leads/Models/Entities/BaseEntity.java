package desarrolloLocal.leads.Models.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@MappedSuperclass
public class BaseEntity {
    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "CreationDate")
    private LocalDateTime creationDate;

}