package ohlim.fooda.dto.user;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TokenPairDto {
    private String accessToken;
    private String refreshToken;

    public static TokenPairDto createTokenPairDto(String accessToken, String refreshToken){
        return TokenPairDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
