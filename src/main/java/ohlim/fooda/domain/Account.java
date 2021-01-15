package ohlim.fooda.domain;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.util.*;

@Data
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable=false, unique=true, length=30, name = "user_name")
    private String userName;

    @Column(length=50)
    @Pattern(regexp = "^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,6}$")
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(length = 30, name = "phone_number")
    @Pattern( regexp ="^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$")
    private String phoneNumber;

    @Column
    @Positive
    private Integer age;

    @Column
    private Character gender;

    @CreationTimestamp
    @Column(name = "register_date")
    private Date registerDate;

    @UpdateTimestamp
    @Column(name = "update_date")
    private Date updateDate;

    private String role;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private Set<Restaurant> restaurants = new LinkedHashSet<Restaurant>();

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private Set<Folder> folders = new LinkedHashSet<Folder>();

}