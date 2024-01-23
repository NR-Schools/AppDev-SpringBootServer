package com.rijai.LocationApi.controller;

import com.rijai.LocationApi.model.Account;
import com.rijai.LocationApi.model.Dog;
import com.rijai.LocationApi.service.IAccountService;
import com.rijai.LocationApi.service.IDogService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "http://localhost:4200")

@RestController
public class DogController {
    @Autowired
    private IAccountService accountService;
    @Autowired
    private IDogService dogService;

    @PostMapping("/api/dog/add-dog")
    public boolean addNewDog(
            @RequestHeader(name = "email", required = false) String email,
            @RequestHeader(name = "session-auth-string", required = false) String sessionAuthString,
            @RequestParam(value = "photoBytes", required = false) MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("breed") String breed,
            @RequestParam("age") int age,
            @RequestParam("sex") String sex,
            @RequestParam("colorCoat") String colorCoat,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("arrivedDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate arrivedDate,
            @RequestParam("arrivedFrom") String arrivedFrom,
            @RequestParam("size") String size,
            @RequestParam("location") String location) {

        // Create Dog
        Dog dog = new Dog();
        try {
            dog.setPhotoBytes(null);
            if (file != null) {
                dog.setPhotoBytes(file.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        dog.setName(name);
        dog.setBreed(breed);
        dog.setAge(age);
        dog.setSex(sex);
        dog.setColorCoat(colorCoat);
        dog.setDescription(description);
        dog.setArrivedDate(arrivedDate);
        dog.setArrivedFrom(arrivedFrom);
        dog.setSize(size);
        dog.setLocation(location);

        // Construct Account
        Account reqAccount = new Account();
        reqAccount.setEmail(email);
        reqAccount.setSessionAuthString(sessionAuthString);

        // Check if Admin
        if (!accountService.isAdmin(reqAccount)) {
            return false;
        }

        // Add Dog
        dogService.addDogRecord(dog);
        return true;
    }

    @GetMapping("/api/dog/dogs")
    public List<Dog> getAllDogs() {
        return dogService.getAllDogRecords();
    }

    @GetMapping("/api/dog/show-dog/{dogId}")
    public Dog getDog(@PathVariable long dogId) {
        return dogService.getDogRecord(dogId);
    }

    @PutMapping("/api/dog/update-dog")
    public Dog updateDog(
            @RequestHeader(name = "email", required = false) String email,
            @RequestHeader(name = "session-auth-string", required = false) String sessionAuthString,
            @RequestParam("id") long id,
            @RequestParam(value = "photoBytes", required = false) MultipartFile file,
            @RequestParam("isPhotoUpdated") boolean isPhotoUpdated,
            @RequestParam("name") String name,
            @RequestParam("breed") String breed,
            @RequestParam("age") int age,
            @RequestParam("sex") String sex,
            @RequestParam("colorCoat") String colorCoat,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("arrivedDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate arrivedDate,
            @RequestParam("arrivedFrom") String arrivedFrom,
            @RequestParam("size") String size,
            @RequestParam("location") String location) {

        // Create Dog
        Dog dog = new Dog();
        dog.setId(id);
        try {
            dog.setPhotoBytes(null);
            if (file != null) {
                dog.setPhotoBytes(file.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        dog.setName(name);
        dog.setBreed(breed);
        dog.setAge(age);
        dog.setSex(sex);
        dog.setColorCoat(colorCoat);
        dog.setDescription(description);
        dog.setArrivedDate(arrivedDate);
        dog.setArrivedFrom(arrivedFrom);
        dog.setSize(size);
        dog.setLocation(location);

        dog.setPhotoChanged(isPhotoUpdated);

        // Construct Account
        Account reqAccount = new Account();
        reqAccount.setEmail(email);
        reqAccount.setSessionAuthString(sessionAuthString);

        // Check if Admin
        if (!accountService.isAdmin(reqAccount)) {
            return null;
        }

        // Add Dog
        return dogService.updateDogRecord(dog);
    }

    @DeleteMapping("/api/dog/delete-dog/{dogId}")
    public boolean deleteDog(
            @RequestHeader(name = "email", required = false) String email,
            @RequestHeader(name = "session-auth-string", required = false) String sessionAuthString,
            @PathVariable long dogId) {

        // Construct Account
        Account reqAccount = new Account();
        reqAccount.setEmail(email);
        reqAccount.setSessionAuthString(sessionAuthString);

        // Check if Admin
        if (!accountService.isAdmin(reqAccount)) {
            return false;
        }

        // Delete Dog
        dogService.deleteDogRecord(dogId);
        return true;
    }

    // For Adoption
    @GetMapping("/api/dog-adopt/user-view-all-adopt-req")
    public List<Dog> userViewAllDogAdoptReq(
            @RequestHeader(name = "email", required = false) String email,
            @RequestHeader(name = "session-auth-string", required = false) String sessionAuthString) {

        // Construct Account
        Account uncheckedAccount = new Account();
        uncheckedAccount.setEmail(email);
        uncheckedAccount.setSessionAuthString(sessionAuthString);

        // Check Account
        if (!accountService.isValidUser(uncheckedAccount)) {
            return List.of();
        }

        // Get Account
        Account reqAccount = accountService.getAccount(email);

        // Find all from email
        return dogService.userViewAllReq(reqAccount.getId());
    }

    @PostMapping("/api/dog-adopt/user-dog-adopt")
    public Dog userDogAdopt(
            @RequestHeader(name = "email", required = false) String email,
            @RequestHeader(name = "session-auth-string", required = false) String sessionAuthString,
            @RequestBody Dog dog) {

        // Construct Account
        Account uncheckedAccount = new Account();
        uncheckedAccount.setEmail(email);
        uncheckedAccount.setSessionAuthString(sessionAuthString);

        // Check Account
        if (!accountService.isValidUser(uncheckedAccount)) {
            return null;
        }

        // Get Account
        Account reqAccount = accountService.getAccount(email);

        Dog reqAdoptedDog = dogService.userAdoptDog(dog, reqAccount);
        reqAdoptedDog.getAccount().setId(-1);
        reqAdoptedDog.getAccount().setPassword("");
        return reqAdoptedDog;
    }

    @PostMapping("/api/dog-adopt/user-cancel-dog-adopt-req")
    public boolean userCancelDogAdoptRequest(
            @RequestHeader(name = "email", required = false) String email,
            @RequestHeader(name = "session-auth-string", required = false) String sessionAuthString,
            @RequestBody Dog dog) {
        
        // Construct Account
        Account uncheckedAccount = new Account();
        uncheckedAccount.setEmail(email);
        uncheckedAccount.setSessionAuthString(sessionAuthString);

        // Check Account
        if (!accountService.isValidUser(uncheckedAccount)) {
            return false;
        }

        // Get Account
        Account reqAccount = accountService.getAccount(email);

        // Try to cancel dog adopt request
        return dogService.userCancelDogAdoptRequest(dog, reqAccount);
    }

    @GetMapping("/api/dog-adopt/admin-view-all-adopt-req")
    public List<Dog> adminViewAllDogAdoptReq(
            @RequestHeader(name = "email", required = false) String email,
            @RequestHeader(name = "session-auth-string", required = false) String sessionAuthString) {

        // Construct Account
        Account reqAccount = new Account();
        reqAccount.setEmail(email);
        reqAccount.setSessionAuthString(sessionAuthString);

        // Check if Admin
        if (!accountService.isAdmin(reqAccount)) {
            return List.of();
        }

        return dogService.adminViewAllDogAdoptReq();
    }

    @PostMapping("/api/dog-adopt/admin-confirm-req")
    public boolean adminConfirmReq(@RequestHeader(name = "email", required = false) String email,
            @RequestHeader(name = "session-auth-string", required = false) String sessionAuthString,
            @RequestBody Dog dog) {

        // Construct Account
        Account reqAccount = new Account();
        reqAccount.setEmail(email);
        reqAccount.setSessionAuthString(sessionAuthString);

        // Check if Admin
        if (!accountService.isAdmin(reqAccount)) {
            return false;
        }

        return dogService.adminConfirmReqDogAdopt(dog);
    }
}
