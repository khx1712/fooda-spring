package ohlim.fooda.repository;

import ohlim.fooda.domain.Account;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, Long> {
    Optional<Account> findByUserName(String userName);
    Account findByEmail(String email);
    Long deleteByUserName(String userName);
}