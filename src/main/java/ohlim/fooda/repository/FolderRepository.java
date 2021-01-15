package ohlim.fooda.repository;

import ohlim.fooda.domain.Account;
import ohlim.fooda.domain.Folder;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FolderRepository extends CrudRepository<Folder, Long> {
    Optional<Folder> findById(Long id);
    List<Folder> findAllByAccount(Account account);
    Optional<Folder> findAllByIdAndAccount(Long id, Account account);
}
