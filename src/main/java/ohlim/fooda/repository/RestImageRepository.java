package ohlim.fooda.repository;

import ohlim.fooda.domain.RestImage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class RestImageRepository {
    @PersistenceContext
    private EntityManager em;

    public RestImage getRestImage(Long id){
        return em.find(RestImage.class, id);
    }

    public void save(RestImage restImage){
        if(restImage.getId() == null){
            em.persist(restImage);
        }else{
            em.merge(restImage);
        }
    }

    public void delete(RestImage restImage){
        em.remove(restImage);
    }

    public List<RestImage> findAllByRestaurantId(Long restaurantId){
        return em.createQuery("select ri from RestImage ri join ri.restaurant r where r.restaurant_id = :restaurantId"
                , RestImage.class)
                .setParameter("restaurantId", restaurantId)
                .getResultList();
    }

//    Optional<RestImage> findAllByIdAndUserName(Long id, String username);
//    List<RestImage> findAllByRestaurantId(Long restaurantId);
//    @Query(value = "SELECT r.file_url FROM rest_Image r WHERE r.restaurant_id like %?1" , nativeQuery = true)
//    List<Object> getFileUrls(Long restaurantId);
}
