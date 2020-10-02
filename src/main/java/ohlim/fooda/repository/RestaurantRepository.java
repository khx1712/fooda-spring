package ohlim.fooda.repository;

import ohlim.fooda.domain.Restaurant;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends CrudRepository<Restaurant, Long> {
    Optional<Restaurant> findById(Long id);
    List<Restaurant> findByUserName(String username);
    List<Restaurant> findByUserNameAndName(String username, String restaurantName);
}
