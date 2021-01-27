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
public class TokenPairDto {
    @ApiModelProperty(required = true)
    @NotNull(message = "accessToken은 필수 값입니다.")
    private String accessToken;
    @ApiModelProperty(required = true)
    @NotNull(message = "refreshToken은 필수 값입니다.")
    private String refreshToken;

    public static TokenPairDto createTokenPairDto(String accessToken, String refreshToken){
        return TokenPairDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
