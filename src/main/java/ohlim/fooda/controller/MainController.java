package ohlim.fooda.controller;

import io.jsonwebtoken.ExpiredJwtException;
import ohlim.fooda.domain.Account;
import ohlim.fooda.domain.Folder;
import ohlim.fooda.domain.Token;
import ohlim.fooda.dto.SuccessResponse;
import ohlim.fooda.jwt.JwtTokenUtil;
import ohlim.fooda.repository.AccountRepository;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;


@RestController
@CrossOrigin
@RequestMapping // This means URL's start with /demo (after Application path)
public class MainController {
    private Logger logger = LoggerFactory.getLogger(ApplicationRunner.class);
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountRepository accountRepository;
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



    @Transactional
    @PostMapping(path="/admin/deleteuser")
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

    @PostMapping(path="/newuser/logout")
    public ResponseEntity<?> logout(
            @RequestBody Map<String, String> m) {

        String userName = null;
        String accessToken = m.get("accessToken");
//        String userName = ((UserDetails)authentication.getPrincipal()).getUsername();
        // TODO : token 만료 exception 처리 나중에 다시 고민해보기
        try {
            userName = jwtTokenUtil.getUsernameFromToken(accessToken);
        } catch (IllegalArgumentException e) {} catch (ExpiredJwtException e) { //expire됐을 때
            userName = e.getClaims().getSubject();
            logger.info("username from expired access token: " + userName);
        }

        // redis 에서 refreshToken 존재 유무 확인하고 삭제
        try {
            if (redisTemplate.opsForValue().get(userName) != null) {
                redisTemplate.delete(userName);
            }
        } catch (IllegalArgumentException e) {
            // TODO : refreshToken 존재 하지 않을때 exception 만들기
            logger.warn("user does not exist");
        }

        //cache logout token for 10 minutes! : accessToken을 10분 뒤에 만료시킨다.
        redisTemplate.opsForValue().set(accessToken, true);
        redisTemplate.expire(accessToken, 10*6*1000, TimeUnit.MILLISECONDS);

        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(path="/newuser/check")
    public Map<String, Object> checker(@RequestBody Map<String, String> m) {
        String username = null;
        Map<String, Object> map = new HashMap<>();
        try {
            username = jwtTokenUtil.getUsernameFromToken(m.get("accessToken"));
        } catch (IllegalArgumentException e) {
            logger.warn("Unable to get JWT Token");
        }
        catch (ExpiredJwtException e) {
        }

        if (username != null) {
            map.put("success", true);
            map.put("username", username);
        } else {
            map.put("success", false);
        }
        return map;
    }

    @PostMapping(path = "/newuser/login")
    public Map<String, Object> login(@RequestBody Map<String, String> m) throws Exception {
        final String username = m.get("username");
        logger.info("test input username: " + username);
        try {
            am.authenticate(new UsernamePasswordAuthenticationToken(username, m.get("password")));
        } catch (Exception e){
            throw e;
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        final String accessToken = jwtTokenUtil.generateAccessToken(userDetails);
        final String refreshToken = jwtTokenUtil.generateRefreshToken(username);

        Token retok = new Token();
        retok.setUsername(username);
        retok.setRefreshToken(refreshToken);

        //generate Token and save in redis
        ValueOperations<String, Object> vop = redisTemplate.opsForValue();
        vop.set(username, retok);

        logger.info("generated access token: " + accessToken);
        logger.info("generated refresh token: " + refreshToken);
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        map.put("accessToken", accessToken);
        map.put("refreshToken", refreshToken);
        return map;
    }


    @PostMapping(path="/newuser/add") // Map ONLY POST Requests
    public Map<String, Object> addNewUser (@RequestBody Account account) {
        System.out.println(account);
        String un = account.getUserName();
        Map<String, Object> map = new HashMap<>();
        System.out.println("회원가입요청 아이디: "+un + "비번: " + account.getPassword());

        if (accountRepository.findByUserName(un).isEmpty()) {
            account.setUserName(un);
            account.setEmail(account.getEmail());
            String adminPattern = "^admin([0-9]+)$";
            if (Pattern.matches(adminPattern, un)) {
                account.setRole("ROLE_ADMIN");
            } else {
                account.setRole("ROLE_USER");
            }

            account.setPassword(bcryptEncoder.encode(account.getPassword()));
            map.put("success", true);
            map.put("msg", "회원가입이 완료되었습니다.");
            accountRepository.save(account);
            folderService.addFolder(Folder.builder()
                    .name("새폴더")
                    .account(account)
                    .build());
            return map;
        } else {
            map.put("success", false);
            map.put("msg", "중복되는 아이디가 존재합니다.");
        }
        return map;
    }

    @PostMapping(path="/newuser/checkid")
    public Map<String, Object> checkId (@RequestBody Map<String, String> m) {
        Map<String, Object> map = new HashMap<>();

        System.out.println("아이디 체크 요청 이메일: " + m.get("username"));
        if (accountRepository.findByUserName(m.get("username")).isEmpty()) map.put("success", true);
        else map.put("success", false);
        return map;
    }

    @PostMapping(path="/newuser/checkemail")
    public Map<String, Object> checkEmail (@RequestBody Map<String, String> m) {
        Map<String, Object> map = new HashMap<>();
        System.out.println("이메일 체크 요청 이메일: " + m.get("email"));

        if (accountRepository.findByEmail(m.get("email")) == null) map.put("success", true);
        else map.put("success", false);
        return map;
    }

    @PostMapping(path="/newuser/refresh")
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
            } else { //refresh token이 없으면
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