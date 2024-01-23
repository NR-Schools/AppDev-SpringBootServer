package com.rijai.LocationApi.controller;

import com.rijai.LocationApi.model.Account;
import com.rijai.LocationApi.service.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:4200")

@RestController
public class AccountController {
    @Autowired
    private IAccountService accountService;

    @PostMapping("/api/account/signup")
    public boolean userSignUp(@RequestBody Account account)
    {
        Account newAccount = accountService.signUp(account);
        if (newAccount == null) return false;
        return true;
    }

    @PostMapping("/api/account/login")
    public Account userLogIn(@RequestBody Account account)
    {
        Account existingAccount = accountService.login(account);

        if (existingAccount == null) return null;
        existingAccount.setPassword("");
        return existingAccount;
    }

    @PostMapping("/api/account/logout")
    public Account userLogOut(@RequestBody Account account)
    {
        Account existingAccount = accountService.logout(account);

        if (existingAccount == null) return null;
        existingAccount.setPassword("");
        return existingAccount;
    }
}
