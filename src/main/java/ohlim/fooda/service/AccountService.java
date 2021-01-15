package ohlim.fooda.service;

import ohlim.fooda.domain.Account;
import ohlim.fooda.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {
    AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }


    public Iterable<Account> getAllAccount() {
        return accountRepository.findAll();
    }

    public Long deleteAccount(String username) {
        return accountRepository.deleteByUserName(username);
    }
}
