package ohlim.fooda.jwt;

import ohlim.fooda.error.ErrorCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    // token의 권한이 맞지 않는경우를 처리해준다
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().println(
                "{ \"success\" : false, "
                + "\"message\" : \"" + ErrorCode.ACCESS_DENIED.getMessage()
                + "\", \"code\" : \"" + ErrorCode.ACCESS_DENIED.getCode()
                + "\", \"status\" : " + ErrorCode.ACCESS_DENIED.getStatus()
                + ", \"errors\" : [ ] }");
    }
}
