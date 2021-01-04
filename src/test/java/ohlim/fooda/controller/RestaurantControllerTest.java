package ohlim.fooda.controller;

import ohlim.fooda.domain.Restaurant;
import ohlim.fooda.service.RestaurantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RestaurantController.class)
class RestaurantControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RestaurantService restaurantService;

    @Test
    void create() throws Exception {
        //Restaurant restaurant = new Restaurant(1234L, "chichi2", "Seoul");
        given(restaurantService.addRestaurant(any())).will(invocation -> {
            Restaurant restaurant = invocation.getArgument(0);
            return Restaurant.builder()
                    .id(1234L)
                    .folderId(1L)
                    .name("chichi")
                    .latitude(11.44)
                    .longitude(11.44)
                    .location("김민지 바보")
                    .category('C')
                    .businessHour("11~12")
                    .build();
        });

        mvc.perform(post("user/restaurant")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\" : \"chichi\", \"location\":\"김민지 바보\", \"category\":\"C\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("location", "user/restaurant/1234"))
                .andExpect(content().string("{}"));

        verify(restaurantService).addRestaurant(any());
    }

    @Test
    void list() {
    }

    @Test
    void detail() {
    }

    @Test
    void detailByName() {
    }
}