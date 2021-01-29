package ohlim.fooda.service;

import ohlim.fooda.domain.Account;
import ohlim.fooda.domain.Folder;
import ohlim.fooda.domain.Restaurant;
import ohlim.fooda.dto.user.AccountDetailDto;
import ohlim.fooda.dto.user.AccountDto;
import ohlim.fooda.dto.user.TokenPairDto;
import ohlim.fooda.error.exception.DuplicateEmailException;
import ohlim.fooda.error.exception.DuplicateUserNameException;
import ohlim.fooda.jwt.JwtTokenUtil;
import ohlim.fooda.repository.AccountRepository;
import ohlim.fooda.repository.FolderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Transactional
class AccountServiceTest {
    @InjectMocks @Spy
    AccountService accountService;
    @Mock
    FolderRepository folderRepository;
    @Mock
    AccountRepository accountRepository;
    @Mock
    PasswordEncoder bcryptEncoder;
    @Mock
    JwtTokenUtil jwtTokenUtil;

    private Account account;

    @BeforeEach
    public void setUp() throws Exception{
        MockitoAnnotations.initMocks(this);
        account = Account.builder()
                .id(1L)
                .gender('M')
                .phoneNumber("010-1234-5678")
                .password("testPassword")
                .userName("testId")
                .age(10)
                .email("test@naver.com")
                .role("ROLE_ADMIN")
                .build();
    }

    @Test
    void loadUserByUsername() {
        when(accountRepository.findByUserName(any())).thenReturn(Optional.of(account));

        UserDetails userDetail = accountService.loadUserByUsername("testId");

        assertEquals("testId", userDetail.getUsername());
        assertTrue(userDetail.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void getAllAccount() {
        List<Account> accounts = new ArrayList<>();
        accounts.add(account);
        when(accountRepository.findAll()).thenReturn(accounts);
        AccountDetailDto accountDetailDto = AccountDetailDto.create(account);

        List<AccountDetailDto> resultDtoList = accountService.getAllAccount();

        assertEquals(1L, resultDtoList.get(0).getId());
        assertEquals("testId", resultDtoList.get(0).getUserName());
    }

    @Test
    void deleteAccount() {
        when(accountRepository.deleteByUserName(any())).thenReturn(1L);

        Long deleteId = accountService.deleteAccount("testId");

        assertEquals(1L, deleteId);
    }

    @Test
    void addAccount() {
        doNothing().when(accountService).checkDuplicateUserName(any());
        when(accountRepository.save(any())).thenReturn(account);
        doNothing().when(folderRepository).save(any());

        Long accountId = accountService.addAccount(AccountDto.create(account));

        verify(folderRepository, times(1)).save(any());
        verify(accountRepository, times(1)).save(any());
        assertEquals(1L, accountId);
    }

    @Test
    void checkDuplicateUserName_중복() {
        when(accountRepository.findByUserName(any())).thenReturn(Optional.of(new Account()));

        assertThrows(DuplicateUserNameException.class, ()-> {
            accountService.checkDuplicateUserName("testId");
        });
    }

    @Test
    void checkDuplicateUserName_중복아님() {
        when(accountRepository.findByUserName(any())).thenReturn(Optional.empty());

        assertDoesNotThrow(()-> {
            accountService.checkDuplicateUserName("testId");
        });
    }

    @Test
    void checkDuplicateEmail_중복() {
        when(accountRepository.findByEmail(any())).thenReturn(Optional.of(new Account()));

        assertThrows(DuplicateEmailException.class, ()-> {
            accountService.checkDuplicateEmail("email@naver.com");
        });
    }

    @Test
    void checkDuplicateEmail_중복아님() {
        when(accountRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertDoesNotThrow(()-> {
            accountService.checkDuplicateEmail("email@naver.com");
        });
    }

    @Test
    void logoutAccount() {
        TokenPairDto tokenPairDto = TokenPairDto.createTokenPairDto("testAccessToken", "testRefreshToken");
        when(jwtTokenUtil.getUsername(any())).thenReturn("testId");

        String newAccessToken = accountService.logoutAccount(tokenPairDto);
    }

    @Test
    void tokenRefresh() {
    }
}