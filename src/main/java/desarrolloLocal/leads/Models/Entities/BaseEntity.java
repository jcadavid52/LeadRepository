package desarrolloLocal.leads.Models.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Setter
@Getter
@MappedSuperclass
public class BaseEntity {
    @Id
    @UuidGenerator
    @Column(length = 36)
    private String id;

    @CreationTimestamp
    @Column(name = "CreationDate")
    private LocalDateTime creationDate;
}