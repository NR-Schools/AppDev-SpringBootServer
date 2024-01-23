package com.rijai.LocationApi.repository;

import com.rijai.LocationApi.model.Dog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DogRepository extends CrudRepository<Dog, Long> {
    @Query(
            value = "SELECT * FROM dogs WHERE adopt_requested=true",
            nativeQuery = true
    )
    List<Dog> getAllRequestedDogs();

    @Query(
            value = "SELECT * FROM dogs WHERE account_id = ?1",
            nativeQuery = true
    )
    List<Dog> getAllDogsAssocWithUser(long id);
}
