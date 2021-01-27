package ohlim.fooda.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import ohlim.fooda.error.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

//    // spring boot에서 filter를 Bean으로 등록해주는 code이다.
//    //    현재 버전에서는 등록하지 않아도 filder를 사용가능한듯하다 나중에 문제 없으면 삭제하자
//    @Bean
//    public FilterRegistrationBean JwtRequestFilterRegistration (JwtRequestFilter filter) {
//        FilterRegistrationBean registration = new FilterRegistrationBean(filter);
//        registration.setEnabled(false);
//        return registration;
//    }


    // "Authentication" 에 담겨오는 accessToken의 유효성을 판단하고,
    // UserDetail이 들어간 Autentication으로 만들어 다음 filter chain으로 넘겨준다.
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String requestTokenHeader = httpServletRequest.getHeader("Authorization");
        logger.info("tokenHeader: " + requestTokenHeader);

        try {
            if (requestTokenHeader != null) {
                if (!requestTokenHeader.startsWith("Bearer ")) {
                    throw new JwtException("Bearer 로 시작하지 않습니다.");
               } else {
                    String jwtToken = requestTokenHeader.substring(7);
                    String username = jwtTokenUtil.getUsername(jwtToken);
                    List<String> roles = jwtTokenUtil.getRoles(jwtToken);
                    if(redisTemplate.opsForValue().get(username) == null){
                        throw new JwtException("이미 로그아웃 되었습니다.");
                    }

                    Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
                    for (String role : roles) {
                        grantedAuthorities.add(new SimpleGrantedAuthority(role));
                    }
                    UserDetails userDetails = User.builder().username(username)
                            .authorities(grantedAuthorities).password("Garbage").build();
                    Authentication authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    httpServletResponse.setHeader("username", username);
                }
            }
        }catch (ExpiredJwtException e){
            e.printStackTrace();
            httpServletRequest.setAttribute("exception", ErrorCode.EXPIRED_ACCESS_TOKEN.getCode());
        }catch (JwtException e){
            e.printStackTrace();
            httpServletRequest.setAttribute("exception", ErrorCode.INVALID_ACCESS_TOKEN.getCode());
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}