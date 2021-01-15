package ohlim.fooda.service;

import ohlim.fooda.domain.Restaurant;
import ohlim.fooda.repository.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
class RestaurantServiceTest {

    @Autowired RestaurantService restaurantService;
    @Autowired RestaurantRepository restaurantRepository;

    @Test
    void getRestaurant() {
    }

    @Test
    void getRestaurantByName() {
    }

    @Test
    public void addRestaurant() throws Exception{
        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .name("테스트")
                .location("조마루로 271")
                .businessHour("영업시간")
                .category('C')
                .phoneNumber("전화번호")
                .thumbnailUrl("아직 없어요")
                .build();
    }

    @Test
    void updateRestaurant() {
    }

    @Test
    void deleteRestaurant() {
    }

    @Test
    void getMapRestaurants() {
    }
}