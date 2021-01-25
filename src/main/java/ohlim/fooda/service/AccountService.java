package ohlim.fooda.service;

import io.jsonwebtoken.ExpiredJwtException;
import ohlim.fooda.domain.Account;
import ohlim.fooda.domain.Folder;
import ohlim.fooda.domain.Token;
import ohlim.fooda.dto.user.AccountDetailDto;
import ohlim.fooda.dto.user.AccountDto;
import ohlim.fooda.dto.user.LoginDto;
import ohlim.fooda.dto.user.TokenPairDto;
import ohlim.fooda.error.exception.DuplicateEmailException;
import ohlim.fooda.error.exception.DuplicateUserNameException;
import ohlim.fooda.error.exception.ExpiredRefreshTokenException;
import ohlim.fooda.error.exception.NoRefreshTokenException;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Service
@Transactional
public class AccountService implements UserDetailsService {
    private Logger logger = LoggerFactory.getLogger(ApplicationRunner.class);

    AccountRepository accountRepository;
    PasswordEncoder bcryptEncoder;
    FolderRepository folderRepository;

    @Autowired
    private AuthenticationManager am;
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

    /**
     * UserDetailsService에 있는 메소드를 override해서 사용한다.
     * 유저의 아이디를 입력받아 아이디에 해당하는 유저를 가져온뒤 JWT, Authentication에서 사용할 UserDetail를 생성 후 반환한다.
     * @param username 유저 아이디
     * @return 아이디에 해당하는 UserDetail
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUserName(username)
                .orElseThrow(()-> new UsernameNotFoundException("User not found with username: " + username));
        List<GrantedAuthority> roles = new ArrayList<>();
        if ((account.getRole()).equals("ROLE_ADMIN")) {
            roles.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            roles.add(new SimpleGrantedAuthority("ROLE_USER"));
            roles.add(new SimpleGrantedAuthority("ROLE_HI"));
        }
        return new User(account.getUserName(), account.getPassword(), roles);
    }

    /**
     * 회원가입된 유저목록을 불러옵니다.
     * @return 등록된 유저 정보 목록
     */
    public List<AccountDetailDto> getAllAccount() {
        Iterable<Account> accounts = accountRepository.findAll();
        List<AccountDetailDto> accountDetailDtoList = new ArrayList<>();
        for(Account account : accounts){
            accountDetailDtoList.add(AccountDetailDto.createAccountDetailDto(account));
        }
        return accountDetailDtoList;
    }

    /**
     * 아이디에 해당하는 유저를 삭제합니다.
     * @param username 사용자 아이디
     * @return 삭제된 user_id
     */
    public Long deleteAccount(String username) {
        return accountRepository.deleteByUserName(username);
    }

    /**
     * 유저정보를 바탕으로 유저를 저장합니다, 기본폴더를 생성합니다.
     * @param accountDto 유저 정보
     * @return 등록된 user_id
     */
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

    /**
     * 유저 아이디가 이미 존재한다면 exception 발생시킵니다.
     * @param userName 유저 아이디
     */
    public void checkDuplicateUserName(String userName) {
        if (accountRepository.findByUserName(userName).isPresent()){
            throw new DuplicateUserNameException();
        }
    }

    /**
     * 유저 이메일이 이미 존재한다면 exception 발생시킵니다.
     * @param email 유저 이메일
     */
    public void checkDuplicateEmail(String email) {
        if(accountRepository.findByEmail(email) != null){
            throw new DuplicateEmailException();
        }
    }

    /**
     * 로그인 정보가 유효하다면 token들을 생성하고 refreshToken을 저장하여 로그인 처리를 해줍니다.
     * @param loginDto 로그인 정보
     * @return accessToken, refreshToken
     */
    public TokenPairDto loginAccount(LoginDto loginDto) {
        final String username = loginDto.getUserName();
        String password = bcryptEncoder.encode(loginDto.getPassword());
        logger.info("test input username: " + username);

        // 아이디, 비밀번호의 유효성 검증
        try {
            am.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (BadCredentialsException e){ // 아이디, 비밀번호가 잘못됬음
            e.printStackTrace();
        }

        // 토큰 생성
        final UserDetails userDetails = loadUserByUsername(username);
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
                accessToken,refreshToken);
    }

    /**
     * 토큰들을 받아 refreshToken을 삭제하여 로그아웃 시킵니다.
     * @param tokenPairDto 토큰 pair
     */
    public void logoutAccount(TokenPairDto tokenPairDto) {
        String userName = null;
        String accessToken = tokenPairDto.getAccessToken();
        // TODO : token 만료 exception 처리 나중에 다시 고민해보기
        try {
            userName = jwtTokenUtil.getUsername(accessToken);
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

    /**
     * 토큰들을 받아 refreshToken으로 새로운 accessToken울 반환합니다.
     * @param tokenPairDto 토큰 pair
     * @return new accessToken, refreshToken
     */
    public TokenPairDto tokenRefresh(TokenPairDto tokenPairDto) {

        String accessToken = tokenPairDto.getAccessToken();
        String refreshToken = tokenPairDto.getRefreshToken();
        String username = jwtTokenUtil.getUsername(accessToken);

        if (refreshToken != null) {
            ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
            Token result = (Token) valueOperations.get(username);
            if(result == null){
                throw new ExpiredRefreshTokenException();
            }
            String refreshTokenFromRedis = result.getRefreshToken();
            logger.info("rtfrom db: " + refreshTokenFromRedis);

            //둘이 일치하고 만료도 안됐으면 재발급
            if (refreshToken.equals(refreshTokenFromRedis) && !jwtTokenUtil.isTokenExpired(refreshToken)) {
                final UserDetails userDetails = loadUserByUsername(username);
                accessToken =  jwtTokenUtil.generateAccessToken(userDetails);
            } else {
                throw new ExpiredRefreshTokenException();
            }
        } else { //refresh token이 없다 재 로그인 해야됨
            throw new NoRefreshTokenException();
        }

        return TokenPairDto.createTokenPairDto(accessToken, refreshToken);
    }
}
