package ohlim.fooda.service;

import ohlim.fooda.domain.Account;
import ohlim.fooda.domain.Folder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class AccountServiceTest {

    @Test
    void loadUserByUsername() {
        System.out.println("test");
    }

    @Test
    void getAllAccount() {
    }

    @Test
    void deleteAccount() {
    }

    @Test
    void addAccount() {
//        Account account = Account.createAccount(accountDto);
//        account.setId(1L);
//        Folder folder = Folder.createFolder(account, "새폴더");

//        verify(folderRepository, times(1)).save(folder);
    }

    @Test
    void checkDuplicateUserName() {
    }

    @Test
    void checkDuplicateEmail() {
    }
}