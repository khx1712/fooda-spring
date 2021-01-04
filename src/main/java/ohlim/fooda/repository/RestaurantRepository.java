package ohlim.fooda.repository;

import ohlim.fooda.domain.Restaurant;
import ohlim.fooda.dto.RestaurantDto.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends CrudRepository<Restaurant, Long> {
    Optional<Restaurant> findById(Long id);
    List<Restaurant> findByUserName(String username);
    List<Restaurant> findByUserNameAndName(String username, String restaurantName);
    Optional<Restaurant> findByUserNameAndId(String username, Long id);
    List<Restaurant> findAllByFolderId(Long folderId);

    @Query(value ="SELECT *, (6371000 * acos( cos( radians(?1) ) * cos( radians( dest.latitude ) )" +
            " * cos( radians( dest.longitude ) - radians(?2) )" +
            " + sin( radians(?1) ) * sin( radians( dest.latitude ) ))) as  distance" +
            " FROM restaurant dest WHERE dest.user_name like %?3 ORDER BY distance" , nativeQuery = true)
    List<Restaurant> getRestaurantOrderByDist(Double latitude, Double Longitude, String username);
}
