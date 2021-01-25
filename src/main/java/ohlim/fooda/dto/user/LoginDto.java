package ohlim.fooda.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@ApiModel
public class LoginDto {
    @ApiModelProperty(value="아이디", required = true)
    private String userName;
    @ApiModelProperty(value="비밀번호", required = true)
    private String password;
}
