package ohlim.fooda.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ohlim.fooda.domain.Restaurant;
import ohlim.fooda.dto.restaurant.RestaurantDetailDto;
import ohlim.fooda.dto.restaurant.RestaurantDto;
import ohlim.fooda.dto.restaurant.RestaurantImageDto;
import ohlim.fooda.dto.restaurant.RestaurantThumbnailDto;
import ohlim.fooda.error.exception.InvalidParameterException;
import ohlim.fooda.service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.mockito.Mock;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.util.NestedServletException;

import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@WebAppConfiguration
class RestaurantControllerTest {
    @MockBean
    RestaurantService restaurantService;
    @Autowired
    RestaurantController restaurantController;
    @Autowired
    WebApplicationContext webApplicationContext;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;
    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);
    private Restaurant restaurant;

    @BeforeEach
    public void setUp() throws Exception{
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilters(new CharacterEncodingFilter("UTF-8", true)).build();
        restaurant = Restaurant.builder()
                .id(1L)
                .name("testRestaurant")
                .businessHour("testHour")
                .latitude(12345.0)
                .longitude(54321.0)
                .thumbnailUrl("testThumbnailUrl")
                .category('K')
                .phoneNumber("testNumber")
                .location("testLocation")
                .restImages(new LinkedHashSet<>())
                .build();
    }

    @Test
    @WithMockUser(username = "testId", roles = {"USER, ADMIN"})
    void create_성공() throws Exception{
        RestaurantDto restaurantDto = RestaurantDto.create(restaurant);
        String body = objectMapper.writeValueAsString(restaurantDto);

        MockMultipartFile file1 = new MockMultipartFile("files", "test1.jpg", "text/plain", "test1 data".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "test2.jpg", "text/plain", "test2 data".getBytes());
        MockMultipartFile resource = new MockMultipartFile("resource", "", "application/json", body.getBytes());

        when(restaurantService.addRestaurant(any(),any(),any())).thenReturn(1L);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/user/restaurant")
                .file(resource)
                .file(file1)
                .file(file2)
                .characterEncoding("UTF-8"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.meta.restaurantId").value(1));

    }

    @Test
    @WithMockUser(username = "testId", roles = {"USER, ADMIN"})
    void create_입력오류() throws Exception{
        RestaurantDto restaurantDto = RestaurantDto.create(restaurant);
        restaurantDto.setFolderId(null);
        restaurantDto.setCategory(null);

        String body = objectMapper.writeValueAsString(restaurantDto);

        MockMultipartFile resource = new MockMultipartFile("resource", "", "application/json", body.getBytes());

        when(restaurantService.addRestaurant(any(),any(),any())).thenReturn(1L);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/user/restaurant")
                .file(resource)
                .characterEncoding("UTF-8"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("ohlim.fooda.error.exception.InvalidParameterException: Invalid Request Data"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("폴더는 필수 값입니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[1].reason").value("카테고리는 필수 값입니다."));
    }

    @Test
    void update_성공() throws Exception{
        RestaurantDto restaurantDto = RestaurantDto.create(restaurant);
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

    @Test
    void delete_성공() throws Exception {

        doNothing().when(restaurantService).deleteRestaurant(any());

        mockMvc.perform(MockMvcRequestBuilders.delete("/user/restaurant/{restaurantId}", 1L))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("'1' 식당을 삭제하였습니다."));
    }

    @Test
    void detail_성공() throws Exception {
        RestaurantDetailDto restaurantDetailDto = RestaurantDetailDto.create(restaurant);
        when(restaurantService.getRestaurant(any())).thenReturn(restaurantDetailDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/user/restaurant/{restaurantId}", 1L))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("'1' 식당의 상세입니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.documents.restaurantId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.documents.name").value("testRestaurant"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.documents.latitude").value(12345.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.documents.location").value("testLocation"));
    }

    @Test
    void detailImage_성공() throws Exception {
        RestaurantImageDto restaurantImageDto = RestaurantImageDto.create(restaurant);
        restaurantImageDto.getImageUrls().add("testImageUrl1");
        restaurantImageDto.getImageUrls().add("testImageUrl2");

        when(restaurantService.getRestaurantIncludeImage(any())).thenReturn(restaurantImageDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/user/restaurant/{restaurantId}/restImages", 1L))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("이미지를 포함한 '1' 식당의 상세입니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.documents.name").value("testRestaurant"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.documents.latitude").value(12345.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.documents.location").value("testLocation"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.documents.imageUrls[0]").value("testImageUrl1"));
    }

    @Test
    @WithMockUser(username = "testId", roles = {"USER, ADMIN"})
    void listByName_성공() throws Exception  {
        List<RestaurantThumbnailDto> restaurantThumbnailDtos = new ArrayList<>();
        restaurantThumbnailDtos.add(RestaurantThumbnailDto.create(restaurant));
        Restaurant restaurant2  = restaurant;
        restaurant2.setId(2L);
        restaurantThumbnailDtos.add(RestaurantThumbnailDto.create(restaurant2));

        when(restaurantService.getRestaurantByName(any(),any())).thenReturn(restaurantThumbnailDtos);

        mockMvc.perform(MockMvcRequestBuilders.get("/user/restaurants")
                .param("name", "testRestaurant"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("'testRestaurant' 이름과 유사한 식당 목록입니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.documents[0].name").value("testRestaurant"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.documents[0].location").value("testLocation"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.documents[0].thumbnailUrl").value("testThumbnailUrl"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.documents[1].id").value(2));
    }

    @Test
    @WithMockUser(username = "testId", roles = {"USER, ADMIN"})
    void listByName_파라미터입력() throws Exception  {

        mockMvc.perform(MockMvcRequestBuilders.get("/user/restaurants"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("org.springframework.web.bind.MissingServletRequestParameterException: Required String parameter 'name' is not present"));
    }

    @Test
    void listGps_성공() throws Exception {
    }
}