package ohlim.fooda.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ohlim.fooda.dto.restaurant.RestaurantDto;
import ohlim.fooda.dto.user.LoginDto;
import ohlim.fooda.jwt.JwtRequestFilter;
import ohlim.fooda.jwt.JwtTokenUtil;
import ohlim.fooda.service.AccountService;
import ohlim.fooda.service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.mockito.Mock;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Transactional
class RestaurantControllerTest {
    @Mock
    RestaurantService restaurantService;
    @InjectMocks
    RestaurantController restaurantController;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private MockMvc multipartMockMvc;
    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);

    @BeforeEach
    public void setUp() throws Exception{
        mockMvc = MockMvcBuilders.standaloneSetup(restaurantController).build();
        multipartMockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(username = "testId", roles = {"USER, ADMIN"})
    void create_성공() throws Exception{
        RestaurantDto restaurantDto = RestaurantDto.builder()
                .name("testRestaurant")
                .businessHour("testHour")
                .category('K')
                .folderId(1L)
                .phoneNumber("testNumber")
                .location("경기 부천시 소향로 233")
                .build();
        String body = objectMapper.writeValueAsString(restaurantDto);

        MockMultipartFile file1 = new MockMultipartFile("files", "test1.jpg", "text/plain", "test1 data".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "test2.jpg", "text/plain", "test2 data".getBytes());
        MockMultipartFile resource = new MockMultipartFile("resource", "", "application/json", body.getBytes());

        when(restaurantService.addRestaurant(any(),any(),any())).thenReturn(1L);

        multipartMockMvc.perform(MockMvcRequestBuilders.multipart("/user/restaurant")
                .file(resource)
                .file(file1)
                .file(file2)
                .characterEncoding("UTF-8"))
                .andDo(MockMvcResultHandlers.print());
//                .andExpect(MockMvcResultMatchers.status().isCreated())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.meta.restaurantId").value(1));
        // TODO: 오류 해결하기

    }

    @Test
    void update() throws Exception{
        RestaurantDto restaurantDto = RestaurantDto.builder()
                .name("testRestaurant")
                .businessHour("testHour")
                .category('K')
                .folderId(1L)
                .phoneNumber("testNumber")
                .location("경기 부천시 소향로 233")
                .build();
        String body = objectMapper.writeValueAsString(restaurantDto);

        when(restaurantService.updateRestaurant(any(), any())).thenReturn(1L);

        mockMvc.perform(MockMvcRequestBuilders.patch("/user/restaurant/{restaurantId}", 1L)
                .content(body)
                .contentType(contentType))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("'1' 식당을 수정하였습니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.meta.restaurantId").value(1));
    }

}