package ohlim.fooda.controller;

import io.jsonwebtoken.ExpiredJwtException;
import ohlim.fooda.domain.Token;
import ohlim.fooda.dto.SuccessResponse;
import ohlim.fooda.dto.user.AccountDto;
import ohlim.fooda.dto.user.LoginDto;
import ohlim.fooda.dto.user.TokenPairDto;
import ohlim.fooda.jwt.JwtTokenUtil;
import ohlim.fooda.repository.AccountRepository;
import ohlim.fooda.repository.FolderRepository;
import ohlim.fooda.service.AccountService;
import ohlim.fooda.service.FolderService;
import ohlim.fooda.service.JwtUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@RestController
@CrossOrigin
public class MainController {
    private Logger logger = LoggerFactory.getLogger(ApplicationRunner.class);
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private FolderRepository folderRepository;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private JwtUserDetailsService userDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private AuthenticationManager am;
    @Autowired
    private PasswordEncoder bcryptEncoder;
    @Autowired
    private FolderService folderService;


    @PostMapping(path="/admin/deleteUser")
    public ResponseEntity<?> deleteUser (@RequestBody Map<String, String> m) {
        return new ResponseEntity<>(SuccessResponse.builder()
                .message("사용자를 삭제하였습니다 : " + m.get("username") + ".")
                .documents(accountService.deleteAccount(m.get("username"))).build()
                ,HttpStatus.OK);
    }

    @GetMapping(path="/admin/users")
    public ResponseEntity<?> getAllUsers() {
        return new ResponseEntity<>(SuccessResponse.builder()
                .message("모든 사용자 리스트입니다.")
                .documents(accountService.getAllAccount()).build()
                ,HttpStatus.OK);
    }

    @PostMapping(path="/newUser/add")
    public ResponseEntity<?> addNewUser (@RequestBody AccountDto accountDto) {
        Long accountId = accountService.addAccount(accountDto);
        return new ResponseEntity<>(
                SuccessResponse.builder()
                        .message("'"+accountId.toString()+"' 유저를 등록하였습니다.")
                        // TODO: Dto 처리해줄지 고민해보기
                        .meta( new HashMap<String, Long>(){{
                            put("accountId", accountId);
                        }}).build()
                , HttpStatus.CREATED);
    }

    @PostMapping(path="/newUser/checkId/{userName}")
    public ResponseEntity<?> checkId (
            @PathVariable("userName") String userName) {
        accountService.checkDuplicateUserName(userName);
        return new ResponseEntity<>(
                SuccessResponse.builder()
                        .message("중복되지 않은 아이디입니다.").build()
                , HttpStatus.OK);
    }

    @PostMapping(path="/newUser/checkEmail/{email}")
    public ResponseEntity<?> checkEmail (
            @PathVariable("email") String email){
        accountService.checkDuplicateEmail(email);
        return new ResponseEntity<>(
                SuccessResponse.builder()
                        .message("중복되지 않은 이메일입니다.").build()
                , HttpStatus.OK);
    }

    @PostMapping(path = "/newUser/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) throws Exception {
        TokenPairDto tokenPairDto = accountService.loginAccount(loginDto);
        return new ResponseEntity<>(
                SuccessResponse.builder()
                        .message("로그인 되었습니다.")
                        .documents(tokenPairDto).build()
                , HttpStatus.OK);
    }

    @PostMapping(path="/newUser/logout")
    public ResponseEntity<?> logout(
            @RequestBody TokenPairDto tokenPairDto
    ) {
        accountService.logoutAccount(tokenPairDto);
        return new ResponseEntity<>(
                SuccessResponse.builder()
                        .message("로그아웃 되었습니다.").build()
                , HttpStatus.OK);
    }

    @PostMapping(path="/newUser/refresh")
    public Map<String, Object>  requestForNewAccessToken(@RequestBody Map<String, String> m) {
        String accessToken = null;
        String refreshToken = null;
        String refreshTokenFromDb = null;
        String username = null;
        Map<String, Object> map = new HashMap<>();
        try {
            accessToken = m.get("accessToken");
            refreshToken = m.get("refreshToken");
            logger.info("access token in rnat: " + accessToken);
            try {
                username = jwtTokenUtil.getUsernameFromToken(accessToken);
            } catch (IllegalArgumentException e) {

            } catch (ExpiredJwtException e) { //expire 됐을 때
                username = e.getClaims().getSubject();
                logger.info("username from expired access token: " + username);
            }

            if (refreshToken != null) { //refresh를 같이 보내면
                try {
                    ValueOperations<String, Object> vop = redisTemplate.opsForValue();
                    Token result = (Token) vop.get(username);
                    refreshTokenFromDb = result.getRefreshToken();
                    logger.info("rtfrom db: " + refreshTokenFromDb);
                } catch (IllegalArgumentException e) {
                    logger.warn("illegal argument!!");
                }
                //둘이 일치하고 만료도 안됐으면 재발급
                if (refreshToken.equals(refreshTokenFromDb) && !jwtTokenUtil.isTokenExpired(refreshToken)) {
                    final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    String newtok =  jwtTokenUtil.generateAccessToken(userDetails);
                    map.put("success", true);
                    map.put("accessToken", newtok);
                } else {
                    map.put("success", false);
                    map.put("msg", "refresh token is expired.");
                }
            } else { //refresh token이 없다 재 로그인 해야됨
                map.put("success", false);
                map.put("msg", "your refresh token does not exist.");
            }

        } catch (Exception e) {
            throw e;
        }
        logger.info("m: " + m);

        return map;
    }

}