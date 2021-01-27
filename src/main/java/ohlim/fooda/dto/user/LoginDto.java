package ohlim.fooda.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class LoginDto {
    @ApiModelProperty(value="아이디", required = true)
    @NotNull(message = "아이디는 필수 값입니다.")
    private String userName;
    @ApiModelProperty(value="비밀번호", required = true)
    @NotNull(message = "비밀번호는 필수 값입니다.")
    private String password;
}
