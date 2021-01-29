package ohlim.fooda.dto.user;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.*;
import ohlim.fooda.domain.Account;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class AccountDto {
    @ApiModelProperty(value="아이디", required = true)
    @NotNull(message = "아이디는 필수 값입니다.")
    private String userName;
    @Email
    private String email;
    @ApiModelProperty(value="비밀번호", required = true)
    @NotNull(message = "비밀번호는 필수 값입니다.")
    private String password;
    @Pattern( regexp ="^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$")
    private String phoneNumber;
    private Integer age;
    private Character gender;

    public static AccountDto create(Account account){
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
