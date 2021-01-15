package ohlim.fooda.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class loginDto {
    private String userName;
    private String password;
}
