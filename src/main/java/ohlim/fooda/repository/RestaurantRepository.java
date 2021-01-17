package ohlim.fooda.repository;

import ohlim.fooda.domain.Account;
import ohlim.fooda.domain.Restaurant;
import ohlim.fooda.dto.restaurant.RestaurantDto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class RestaurantRepository{

    @PersistenceContext
    private EntityManager em;

    public Restaurant getRestaurant(Long id){
        return em.find(Restaurant.class, id);
    }

    public void save(Restaurant restaurant){
        if(restaurant.getId() == null){
            em.persist(restaurant);
        }else{
            em.merge(restaurant);
        }
    }

    public Restaurant getRestaurantImage(Long id){
        return em.createQuery("select distinct r from Restaurant r join fetch r.restImages where r.id = :restaurantId"
                , Restaurant.class).setParameter("restaurantId", id).getSingleResult();
    }

    public List<Restaurant> findByUserNameAndRestName(String userName, String restaurantName){
        return em.createQuery("select r from Restaurant r join r.account a " +
                        "where r.name = :restaurantName and " +
                        "a.userName = :userName", Restaurant.class)
                .setParameter("restaurantName", restaurantName)
                .setParameter("userName", userName).getResultList();
    }

    public Restaurant findByUserNameAndId(String userName, Long id){
        return em.createQuery("select r from Restaurant r join r.account a " +
                "where r.id = :restaurantId and " +
                "a.userName = :userName", Restaurant.class)
                .setParameter("restaurantId", id)
                .setParameter("userName", userName).getSingleResult();
    }

    public List<Restaurant> findByFolderId(Long folderId){
        return em.createQuery("select r from Restaurant r join r.folder f " +
                "where f.id = :folderId", Restaurant.class)
                .setParameter("folderId", folderId).getResultList();
    }

    public void delete(Restaurant restaurant){
        em.remove(restaurant);
    }

    public List<Restaurant> getRestaurantOrderByDist(Double latitude, Double longitude, Long userId){
        String sql = "SELECT *, (6371000 * acos( cos( radians(:latitude) ) * cos( radians( dest.latitude ) )" +
                " * cos( radians( dest.longitude ) - radians(:longitude) )" +
                " + sin( radians(:latitude) ) * sin( radians( dest.latitude ) ))) as  distance" +
                " FROM restaurant dest WHERE dest.user_id like :userId ORDER BY distance";
        return em.createNativeQuery(sql, Restaurant.class)
                .setParameter("latitude", latitude)
                .setParameter("longitude", longitude)
                .setParameter("userId", userId).getResultList();
    }




//    Optional<Restaurant> findById(Long id);
//    List<Restaurant> findByAccountAndName(Account account, String restaurantName);
//    Optional<Restaurant> findByAccountAndId(Account account, Long id);
//    List<Restaurant> findAllByFolderId(Long folderId);
//
//    @Query(value ="SELECT *, (6371000 * acos( cos( radians(?1) ) * cos( radians( dest.latitude ) )" +
//            " * cos( radians( dest.longitude ) - radians(?2) )" +
//            " + sin( radians(?1) ) * sin( radians( dest.latitude ) ))) as  distance" +
//            " FROM restaurant dest WHERE dest.user_name like %?3 ORDER BY distance" , nativeQuery = true)
//    List<Restaurant> getRestaurantOrderByDist(Double latitude, Double Longitude, String username);
//
//    @Query(value = "SELECT DISTINCT r FROM restaurant r LEFT JOIN FETCH r.restImages" +
//            " ON r.restaurant_id Like %?1", nativeQuery = true)
//    Optional<Restaurant> getRestaurantImage(Long id);
}
