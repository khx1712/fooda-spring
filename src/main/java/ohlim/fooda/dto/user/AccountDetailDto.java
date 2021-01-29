package ohlim.fooda.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ohlim.fooda.domain.Account;

@Getter
@Setter
@Builder
public class AccountDetailDto {
    private Long id;
    private String userName;
    private String email;
    private String password;
    private String phoneNumber;
    private Integer age;
    private Character gender;

    public static AccountDetailDto create(Account account){
        // TODO: ModelMapper 처라하기
        return AccountDetailDto.builder()
                .id(account.getId())
                .userName(account.getUserName())
                .email(account.getEmail())
                .password(account.getPassword())
                .phoneNumber(account.getPhoneNumber())
                .age(account.getAge())
                .gender(account.getGender())
                .build();
    }
}