package ohlim.fooda.domain;

import javassist.NotFoundException;
import lombok.*;
import ohlim.fooda.dto.restaurant.RestaurantDto;
import ohlim.fooda.dto.user.AccountDto;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.json.simple.parser.ParseException;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.util.*;

@Data
@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    public static Account createAccount(AccountDto accountDto){
        // TODO: ModelMapper 처리해주기
        return Account.builder()
                .userName(accountDto.getUserName())
                .password(accountDto.getPassword())
                .phoneNumber(accountDto.getPhoneNumber())
                .age(accountDto.getAge())
                .email(accountDto.getEmail())
                .gender(accountDto.getGender())
                .folders(new LinkedHashSet<>())
                .restaurants(new LinkedHashSet<>())
                .build();
    }

}