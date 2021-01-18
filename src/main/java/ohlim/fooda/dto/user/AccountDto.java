package ohlim.fooda.dto.user;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AccountDto {
    private String userName;
    private String email;
    private String password;
    private String phoneNumber;
    private Integer age;
    private Character gender;
}
