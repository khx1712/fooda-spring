package ohlim.fooda.repository;

import ohlim.fooda.domain.Account;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, Integer> {
    Optional<Account> findByUserName(String username);
    Account findByEmail(String email);
    Long deleteByUserName(String username);
}