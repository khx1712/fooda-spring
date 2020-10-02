package ohlim.fooda.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.util.Date;

@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable=false)
    private String userName;

    @Column(nullable=false)
    private Long folderId;

    @Column(nullable=false)
    private String name;

    @Column(length = 30)
    private String phoneNumber;

    @Column
    private Double lat;

    @Column
    private Double lon;

    @Column
    private String location;

    @Column
    private Character category;

    @Column
    private String businessHour;

    @CreationTimestamp
    private Date regdate;

    @UpdateTimestamp
    private Date updatedate;
}
