package ohlim.fooda.dto.user;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ohlim.fooda.domain.Account;

@Getter
@Setter
@Builder
@ApiModel
public class AccountDto {
    @ApiModelProperty(value="아이디", required = true)
    private String userName;
    private String email;
    @ApiModelProperty(value="비밀번호", required = true)
    private String password;
    private String phoneNumber;
    private Integer age;
    private Character gender;

    public static AccountDto createAccountDto(Account account){
        // TODO: ModelMapper 처라하기
        return AccountDto.builder()
                .userName(account.getUserName())
                .email(account.getEmail())
                .password(account.getPassword())
                .phoneNumber(account.getPhoneNumber())
                .age(account.getAge())
                .gender(account.getGender())
                .build();
    }
}
