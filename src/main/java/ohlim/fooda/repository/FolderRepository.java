package ohlim.fooda.repository;

import ohlim.fooda.domain.Account;
import ohlim.fooda.domain.Folder;
import ohlim.fooda.domain.Restaurant;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class FolderRepository{
    @PersistenceContext
    private EntityManager em;

    public Folder getFolder(Long id){
        return em.find(Folder.class, id);
    }

    public void save(Folder folder){
        if(folder.getId() == null){
            em.persist(folder);
        }else{
            em.merge(folder);
        }
    }

    public Folder findByIdAndUserName(Long id, String userName){
        return em.createQuery("select f from Folder f join f.account a" +
                "where f.id = :folderId and " +
                "a.userName = :userName", Folder.class)
                .setParameter("folderId", id)
                .setParameter("userName", userName)
                .getSingleResult();
    }

    public void delete(Folder folder){
        em.remove(folder);
    }

    public List<Folder> findByUserName(String userName){
        return em.createQuery("select f from Folder f join f.account a" +
                "where a.userName = :userName", Folder.class)
                .setParameter("userName", userName)
                .getResultList();
    }

    public Folder getFolderRestaurant(Long id){
        return em.createQuery("select distinct f from Folder f join fetch f.restaurants where f.id = :folderId"
                , Folder.class).setParameter("folderId", id).getSingleResult();
    }
}

//public interface FolderRepository extends CrudRepository<Folder, Long> {
//    Optional<Folder> findById(Long id);
//    List<Folder> findAllByAccount(Account account);
//    Optional<Folder> findAllByIdAndAccount(Long id, Account account);
//}
