package ohlim.fooda.repository;

import ohlim.fooda.domain.Folder;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface FolderRepository extends CrudRepository<Folder, Long> {
    Optional<Folder> findAllByIdAndUserName(Long id, String userName);
}
