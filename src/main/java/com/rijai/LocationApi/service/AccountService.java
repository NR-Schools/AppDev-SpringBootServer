package com.rijai.LocationApi.service;

import com.rijai.LocationApi.model.Account;
import com.rijai.LocationApi.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
public class AccountService implements IAccountService {
    @Autowired
    private AccountRepository repository;

    @Override
    public Account signUp(Account newAccount) {
        // Check for duplicate email
        Account opt_account = repository.findByEmail(newAccount.getEmail());
        if (opt_account != null)
            return null;

        // Add and return
        return repository.save(newAccount);
    }

    @Override
    public Account login(Account existingAccount) {
        // Check if email exists
        Account opt_account = repository.findByEmail(existingAccount.getEmail());
        if (opt_account == null)
            return null;

        // No Empty Passwords
        if (existingAccount.getPassword().equals("")) {
            return null;
        }

        // Compare Passwords
        if (!opt_account.getPassword().equals(existingAccount.getPassword())) {
            return null;
        }

        // Generate session auth string
        // String format: 16 Random Characters + Date (yyyymmdd) + time (hhmmss)
        String randomChars = new Random().ints(16, 0, 62)
                .collect(StringBuilder::new,
                        (sb, value) -> sb
                                .append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".charAt(value)),
                        StringBuilder::append)
                .toString();
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String sessionAuthString = randomChars + dateTime;

        // Update and return
        opt_account.setSessionAuthString(sessionAuthString);
        return repository.save(opt_account);
    }

    @Override
    public Account logout(Account existingAccount) {
        // Check if email exists
        Account opt_account = repository.findByEmail(existingAccount.getEmail());
        if (opt_account == null)
            return null;

        // Update and return
        opt_account.setSessionAuthString("");
        return repository.save(opt_account);
    }

    @Override
    public boolean isAdmin(Account possiblyAdminAccount) {
        // Check if non-null
        if (possiblyAdminAccount == null)
            return false;

        // Check email
        if (!possiblyAdminAccount.getEmail().equals("Admin"))
            return false;

        // Check session_auth_string
        Account admin_act = repository.findByEmail(possiblyAdminAccount.getEmail());
        return admin_act.getSessionAuthString().equals(possiblyAdminAccount.getSessionAuthString());
    }

    @Override
    public boolean isValidUser(Account possiblyValidUserAccount) {
        // Check if non-null
        if (possiblyValidUserAccount == null)
            return false;

        Account userAccount = repository.findByEmail(possiblyValidUserAccount.getEmail());

        if (userAccount == null)
            return false;

        // Check session_auth_string
        return userAccount.getSessionAuthString().equals(possiblyValidUserAccount.getSessionAuthString());
    }

    @Override
    public Account getAccount(String email) {
        return repository.findByEmail(email);
    }
}
