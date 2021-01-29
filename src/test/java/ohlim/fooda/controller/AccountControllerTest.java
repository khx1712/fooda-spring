package ohlim.fooda.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ohlim.fooda.dto.user.AccountDto;
import ohlim.fooda.dto.user.LoginDto;
import ohlim.fooda.dto.user.TokenPairDto;
import ohlim.fooda.error.exception.DuplicateEmailException;
import ohlim.fooda.error.exception.DuplicateUserNameException;
import ohlim.fooda.repository.FolderRepository;
import ohlim.fooda.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Transactional
class AccountControllerTest {
    @Mock
    AccountService accountService;
    @InjectMocks
    AccountController accountController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;
    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);

    @BeforeEach
    public void setUp() throws Exception{
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
    }

    @Test()
    void addNewUser_성공() throws Exception{

        AccountDto accountDto = AccountDto.builder()
                .userName("testId")
                .password("testPassword").build();

        when(accountService.addAccount(any())).thenReturn(1L);

        String body = objectMapper.writeValueAsString(accountDto);
        mockMvc.perform( MockMvcRequestBuilders.post("/newUser/add")
                .content(body)
                .contentType(contentType))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.meta.accountId").value(1));
    }

    @Test()
    void addNewUser_입력정보확인() throws Exception{

        AccountDto accountDto = AccountDto.builder()
                .phoneNumber("010-1-13-234-5678")
                .email("email@@@@naver.com")
                .gender('M').build();
        String body = objectMapper.writeValueAsString(accountDto);

        Throwable throwable = assertThrows(NestedServletException.class,()-> {
            mockMvc.perform(MockMvcRequestBuilders.post("/newUser/add")
                    .content(body)
                    .contentType(contentType));
        });
        assertEquals(throwable.getCause().getMessage(), "Invalid Request Data");
    }

    @Test
    void checkId_중복() throws Exception {
        doThrow(new DuplicateUserNameException()).when(accountService).checkDuplicateUserName(eq("testId"));

        Throwable throwable = assertThrows(NestedServletException.class,()-> {
            mockMvc.perform(MockMvcRequestBuilders.post("/newUser/checkId/{userName}", "testId"));
        });
        assertEquals(throwable.getCause().getMessage(), "Duplicate UserName");
    }

    @Test
    void checkEmail_중복() throws Exception {
        doThrow(new DuplicateEmailException()).when(accountService).checkDuplicateEmail(eq("email@naver.com"));

        Throwable throwable = assertThrows(NestedServletException.class,()-> {
            mockMvc.perform(MockMvcRequestBuilders.post("/newUser/checkEmail")
                    .param("email", "email@naver.com"));
        });
        assertEquals(throwable.getCause().getMessage(), "Duplicate Email");
    }

    @Test
    void login_아이디비밀번호() throws Exception {
        LoginDto loginDto = LoginDto.builder().build();
        String loginBody = objectMapper.writeValueAsString(loginDto);

        Throwable throwable = assertThrows(NestedServletException.class, ()->{
            mockMvc.perform(MockMvcRequestBuilders.post("/newUser/login")
                    .content(loginBody)
                    .contentType(contentType));
        });
        assertEquals(throwable.getCause().getMessage(), "Invalid Request Data");
    }

    @Test
    void login_성공() throws Exception {
        TokenPairDto tokenPairDto = TokenPairDto.createTokenPairDto("testAccessToken", "testRefreshToken");
        LoginDto loginDto = LoginDto.builder()
                .userName("testId")
                .password("testPassword")
                .build();
        String loginBody = objectMapper.writeValueAsString(loginDto);

        when(accountService.loginAccount(any())).thenReturn(tokenPairDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/newUser/login")
                .content(loginBody)
                .contentType(contentType))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("'testId'가 로그인 되었습니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.documents.accessToken").value("testAccessToken"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.documents.refreshToken").value("testRefreshToken"));
    }

    @Test
    void logout_성공() throws Exception{
        TokenPairDto tokenPairDto = TokenPairDto.createTokenPairDto("testAccessToken", "testRefreshToken");
        String logoutBody = objectMapper.writeValueAsString(tokenPairDto);

        when(accountService.logoutAccount(any())).thenReturn("testId");

        mockMvc.perform(MockMvcRequestBuilders.post("/newUser/logout")
                .content(logoutBody)
                .contentType(contentType))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("'testId'가 로그아웃 되었습니다."));
    }

    @Test
    void logout_토큰() throws Exception{
        TokenPairDto tokenPairDto = TokenPairDto.builder().build();
        String logoutBody = objectMapper.writeValueAsString(tokenPairDto);

        Throwable throwable = assertThrows(NestedServletException.class, ()->{
            mockMvc.perform(MockMvcRequestBuilders.post("/newUser/logout")
                    .content(logoutBody)
                    .contentType(contentType));
        });
        assertEquals(throwable.getCause().getMessage(), "Invalid Request Data");
    }

    @Test
    void requestForNewAccessToken() throws Exception{
        TokenPairDto tokenPairDto = TokenPairDto.createTokenPairDto("testAccessToken", "testRefreshToken");
        TokenPairDto newTokenPairDto = TokenPairDto.createTokenPairDto("newAccessToken", "newRefreshToken");
        String refreshBody = objectMapper.writeValueAsString(tokenPairDto);

        when(accountService.tokenRefresh(any())).thenReturn(newTokenPairDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/newUser/refresh")
                .content(refreshBody)
                .contentType(contentType))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.documents.accessToken").value("newAccessToken"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.documents.refreshToken").value("newRefreshToken"));
    }

    @Test
    void deleteUser() {
    }

    @Test
    void getAllUsers() {
    }
}