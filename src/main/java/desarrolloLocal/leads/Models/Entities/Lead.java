package desarrolloLocal.leads.Models.Entities;

import desarrolloLocal.leads.Models.Enums.FontType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "Leads")
public class Lead extends BaseEntity{

    @Id
    @UuidGenerator
    private String id;

    @Column(length = 100)
    private String name;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String phoneNumber;

    @Column(length = 200)
    private String interestingProduct;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private FontType font;

}