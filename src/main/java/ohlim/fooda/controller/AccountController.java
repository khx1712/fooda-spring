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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
public class AccountController {
    private Logger logger = LoggerFactory.getLogger(ApplicationRunner.class);
    @Autowired
    private AccountService accountService;


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
                        .message("'"+loginDto.getUserName()+"'가 로그인 되었습니다.")
                        .documents(tokenPairDto).build()
                , HttpStatus.OK);
    }

    @PostMapping(path="/user/logout")
    public ResponseEntity<?> logout(
            @RequestBody TokenPairDto tokenPairDto
    ) {
        accountService.logoutAccount(tokenPairDto);
        return new ResponseEntity<>(
                SuccessResponse.builder()
                        .message("로그아웃 되었습니다.").build()
                , HttpStatus.OK);
    }

    @PostMapping(path="/user/refresh")
    public ResponseEntity<?>  requestForNewAccessToken(
            @RequestBody TokenPairDto tokenPairDto
    ) {
        TokenPairDto newTokenPairDto = accountService.tokenRefresh(tokenPairDto);
        return new ResponseEntity<>(
                SuccessResponse.builder()
                        .message("새로운 accessToken입니다.")
                        .documents(newTokenPairDto)
                        .build()
                , HttpStatus.OK);
    }

}