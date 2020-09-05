package ohlim.fooda.repository;

import ohlim.fooda.domain.Account;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, Integer> {
    Optional<Account> findByUsername(String username);

    Account findByEmail(String email);
    Long deleteByUsername(String username);
}