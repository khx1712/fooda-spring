package ohlim.fooda.jwt;

import ohlim.fooda.error.ErrorCode;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    private static final long serialVersionUID = -7858869558953243875L;

    // filter에서 넘긴 exception을 처리해준다.
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                         AuthenticationException authenticationException) throws IOException {
        String exception = (String)httpServletRequest.getAttribute("exception");
        if(exception == null){
            setErrorResponse(httpServletResponse, ErrorCode.NON_LOGIN);
        }else if(exception.equals(ErrorCode.EXPIRED_ACCESS_TOKEN.getCode())){
            setErrorResponse(httpServletResponse, ErrorCode.EXPIRED_ACCESS_TOKEN);
        }else if(exception.equals(ErrorCode.INVALID_ACCESS_TOKEN.getCode())){
            setErrorResponse(httpServletResponse, ErrorCode.INVALID_ACCESS_TOKEN);
        }

    }

    // error message response를 ErrorCode에 맞게 생성해준다.
    private void setErrorResponse(HttpServletResponse httpServletResponse, ErrorCode errorCode) throws IOException {
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpServletResponse.getWriter().println(
                "{ \"success\" : false, "
                + "\"message\" : \"" + errorCode.getMessage()
                + "\", \"code\" : \"" +  errorCode.getCode()
                + "\", \"status\" : " + errorCode.getStatus()
                + ", \"errors\" : [ ] }");
    }
}