package com.rijai.LocationApi.service;

import com.rijai.LocationApi.model.Account;
import com.rijai.LocationApi.model.Dog;

import java.util.List;

public interface IDogService {
    Dog addDogRecord(Dog newDog);
    List<Dog> getAllDogRecords();
    Dog getDogRecord(long dogId);
    Dog updateDogRecord(Dog updatedDog);
    Dog deleteDogRecord(long dogId);

    // For Adoption
    List<Dog> userViewAllReq(long id);
    Dog userAdoptDog(Dog dog, Account account);
    boolean userCancelDogAdoptRequest(Dog dog, Account account);
    List<Dog> adminViewAllDogAdoptReq();
    boolean adminConfirmReqDogAdopt(Dog dog);
}
