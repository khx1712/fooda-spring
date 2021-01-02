package ohlim.fooda.repository;

import ohlim.fooda.domain.RestImage;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RestImageRepository extends CrudRepository<RestImage, Long> {
    Optional<RestImage> findAllByIdAndUserName(Long id, String username);
    List<RestImage> findAllByRestaurantId(Long restaurantId);
    List<String> findFileUrlByRestaurantId(Long restaurantId);
}
