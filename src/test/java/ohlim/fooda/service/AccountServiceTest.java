package ohlim.fooda.service;

import ohlim.fooda.domain.Account;
import ohlim.fooda.domain.Folder;
import ohlim.fooda.domain.Restaurant;
import ohlim.fooda.dto.user.AccountDto;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.nio.charset.StandardCharsets;
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

    private AccountDto accountDto;

    @BeforeEach
    public void setUp() throws Exception{
        MockitoAnnotations.initMocks(this);
        accountDto = AccountDto.builder()
                .gender('M')
                .phoneNumber("010-1234-5678")
                .password("testPassword")
                .userName("testId")
                .age(10)
                .email("test@naver.com")
                .build();
    }

    @Test
    void loadUserByUsername() {
        System.out.println("test");
    }

    @Test
    void getAllAccount() {
    }

    @Test
    void deleteAccount() {
    }

    @Test
    void addAccount() {
        Account account = Account.createAccount(accountDto);
        account.setId(1L);

        doNothing().when(accountService).checkDuplicateUserName(any());
        when(accountRepository.save(any())).thenReturn(account);
        doNothing().when(folderRepository).save(any());

        Long accountId = accountService.addAccount(accountDto);

        verify(folderRepository, times(1)).save(any());
        verify(accountRepository, times(1)).save(any());
        assertEquals(1L, accountId);
    }

    @Test
    void checkDuplicateUserName() {
    }

    @Test
    void checkDuplicateEmail() {
    }
}