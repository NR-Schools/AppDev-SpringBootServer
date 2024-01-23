package com.rijai.LocationApi.service;

import com.rijai.LocationApi.model.Account;
import com.rijai.LocationApi.model.Dog;
import com.rijai.LocationApi.repository.DogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DogService implements IDogService {
    @Autowired
    private DogRepository repository;

    @Override
    public Dog addDogRecord(Dog newDog) {
        newDog.setAdoptRequested(false);
        newDog.setAdoptAccepted(false);
        return repository.save(newDog);
    }

    @Override
    public List<Dog> getAllDogRecords() {
        return (List<Dog>) repository.findAll();
    }

    @Override
    public Dog getDogRecord(long dogId) {
        Optional<Dog> opt_dog = repository.findById(dogId);
        return opt_dog.orElse(null);
    }

    @Override
    public Dog updateDogRecord(Dog updatedDog) {
        Optional<Dog> opt_dog = repository.findById(updatedDog.getId());
        if (opt_dog.isEmpty())
            return null;

        updatedDog.setAccount(opt_dog.get().getAccount());

        // Exclude Adoption Details
        updatedDog.setAdoptRequested(opt_dog.get().isAdoptRequested());
        updatedDog.setAdoptAccepted(opt_dog.get().isAdoptAccepted());
        updatedDog.setAccount(opt_dog.get().getAccount());

        // If Photo Updated, Proceed As Usual
        // Else, Use Stored
        if (updatedDog.isPhotoChanged()) {
            return repository.save(updatedDog);
        } else {
            updatedDog.setPhotoBytes(opt_dog.get().getPhotoBytes());
            return repository.save(updatedDog);
        }
    }

    @Override
    public Dog deleteDogRecord(long dogId) {
        Optional<Dog> opt_dog = repository.findById(dogId);
        if (opt_dog.isEmpty())
            return null;

        repository.delete(opt_dog.get());
        return opt_dog.get();
    }

    @Override
    public List<Dog> userViewAllReq(long id) {
        return repository.getAllDogsAssocWithUser(id);
    }

    @Override
    public Dog userAdoptDog(Dog dog, Account account) {
        // Get Dog
        Optional<Dog> opt_dog = repository.findById(dog.getId());
        if (opt_dog.isEmpty())
            return null;

        Dog requestedDog = opt_dog.get();

        // If dog is already requested, exit immediately
        if (requestedDog.isAdoptRequested()) return null;

        requestedDog.setAccount(account);
        requestedDog.setAdoptRequested(true);
        requestedDog.setAdoptAccepted(false);
        return repository.save(requestedDog);
    }

    @Override
    public boolean userCancelDogAdoptRequest(Dog dog, Account account) {
        // Remove Information [About User]

        // Get All Dog Info
        Optional<Dog> opt_dog = repository.findById(dog.getId());

        if (opt_dog.isEmpty())
            return false;

        // Check if account id is same as account on dog
        if (opt_dog.get().getAccount() == null)
            return false;
        if (account.getId() != opt_dog.get().getAccount().getId())
            return false;

        Dog requestedDog = opt_dog.get();
        requestedDog.setAccount(null);
        requestedDog.setAdoptRequested(false);
        requestedDog.setAdoptAccepted(false);
        repository.save(requestedDog);

        return true;
    }

    @Override
    public List<Dog> adminViewAllDogAdoptReq() {
        return repository.getAllRequestedDogs();
    }

    @Override
    public boolean adminConfirmReqDogAdopt(Dog dog) {
        // Get Dog
        Optional<Dog> opt_dog = repository.findById(dog.getId());
        if (opt_dog.isEmpty())
            return false;

        Dog requestedDog = opt_dog.get();
        requestedDog.setAdoptAccepted(dog.isAdoptAccepted());
        requestedDog.setAdoptRequested(dog.isAdoptRequested());

        if (!requestedDog.isAdoptRequested()) {
            return false;
        }

        // Check if Accepted Or Not
        if (requestedDog.isAdoptAccepted()) {
            // Remove This Dog
            repository.delete(requestedDog);
        } else {
            // Remove Adoption Info
            requestedDog.setAccount(null);
            requestedDog.setAdoptRequested(false);
            requestedDog.setAdoptAccepted(false);
            repository.save(requestedDog);
        }

        return true;
    }
}
