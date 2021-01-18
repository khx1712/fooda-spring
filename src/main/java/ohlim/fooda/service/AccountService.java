package ohlim.fooda.service;

import io.jsonwebtoken.ExpiredJwtException;
import ohlim.fooda.domain.Account;
import ohlim.fooda.domain.Folder;
import ohlim.fooda.domain.Token;
import ohlim.fooda.dto.user.AccountDto;
import ohlim.fooda.dto.user.LoginDto;
import ohlim.fooda.dto.user.TokenPairDto;
import ohlim.fooda.error.exception.DuplicateEmailException;
import ohlim.fooda.error.exception.DuplicateUserNameException;
import ohlim.fooda.jwt.JwtTokenUtil;
import ohlim.fooda.repository.AccountRepository;
import ohlim.fooda.repository.FolderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Service
@Transactional
public class AccountService {
    private Logger logger = LoggerFactory.getLogger(ApplicationRunner.class);

    AccountRepository accountRepository;
    PasswordEncoder bcryptEncoder;
    FolderRepository folderRepository;

    @Autowired
    private AuthenticationManager am;
    @Autowired
    private JwtUserDetailsService userDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public AccountService(AccountRepository accountRepository, PasswordEncoder bcryptEncoder
            , FolderRepository folderRepository){
        this.accountRepository = accountRepository;
        this.bcryptEncoder = bcryptEncoder;
        this.folderRepository = folderRepository;
    }


    public Iterable<Account> getAllAccount() {
        return accountRepository.findAll();
    }

    public Long deleteAccount(String username) {
        return accountRepository.deleteByUserName(username);
    }

    public Long addAccount(AccountDto accountDto) {
        checkDuplicateUserName(accountDto.getUserName());
        Account account = Account.createAccount(accountDto);
        String adminPattern = "^admin([0-9]+)$";
        if (Pattern.matches(adminPattern, account.getUserName())) {
            account.setRole("ROLE_ADMIN");
        } else {
            account.setRole("ROLE_USER");
        }
        account.setPassword(bcryptEncoder.encode(account.getPassword()));
        accountRepository.save(account);
        folderRepository.save(Folder.createFolder(account, "새폴더"));
        return account.getId();
    }

    public void checkDuplicateUserName(String userName) {
        if (accountRepository.findByUserName(userName).isPresent()){
            throw new DuplicateUserNameException();
        }
    }

    public void checkDuplicateEmail(String email) {
        if(accountRepository.findByEmail(email) != null){
            throw new DuplicateEmailException();
        }
    }

    public TokenPairDto loginAccount(LoginDto loginDto) {
        final String username = loginDto.getUserName();
        logger.info("test input username: " + username);

        // 아이디, 비밀번호의 유효성 검증
        try {
            am.authenticate(new UsernamePasswordAuthenticationToken(username, loginDto.getPassword()));
        } catch (Exception e){
            throw e;
        }

        // 토큰 생성
        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        final String accessToken = jwtTokenUtil.generateAccessToken(userDetails);
        final String refreshToken = jwtTokenUtil.generateRefreshToken(username);

        // reFresh 토큰을 생성
        Token retok = new Token();
        retok.setUsername(username);
        retok.setRefreshToken(refreshToken);

        // userName을 key로 redis에 저장
        ValueOperations<String, Object> vop = redisTemplate.opsForValue();
        vop.set(username, retok);

        return TokenPairDto.createTokenPairDto(
                jwtTokenUtil.generateAccessToken(userDetails),jwtTokenUtil.generateRefreshToken(username));
    }

    public void logoutAccount(TokenPairDto tokenPairDto) {
        String userName = null;
        String accessToken = tokenPairDto.getAccessToken();
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
    }
}
