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
public class TokenPairDto {
    @ApiModelProperty(required = true)
    private String accessToken;
    @ApiModelProperty(required = true)
    private String refreshToken;

    public static TokenPairDto createTokenPairDto(String accessToken, String refreshToken){
        return TokenPairDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
