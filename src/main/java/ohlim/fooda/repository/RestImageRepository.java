package ohlim.fooda.repository;

import ohlim.fooda.domain.RestImage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RestImageRepository extends CrudRepository<RestImage, Long> {
    Optional<RestImage> findAllByIdAndUserName(Long id, String username);
    List<RestImage> findAllByRestaurantId(Long restaurantId);
    @Query(value = "SELECT r.file_url FROM rest_Image r WHERE r.restaurant_id like %?1" , nativeQuery = true)
    List<Object> getFileUrls(Long restaurantId);
}
