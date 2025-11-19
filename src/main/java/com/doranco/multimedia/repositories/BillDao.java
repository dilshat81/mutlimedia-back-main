package com.doranco.multimedia.repositories;

import com.doranco.multimedia.models.Bill;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface BillDao extends MongoRepository<Bill, String> {

    Optional<Bill> findByUuid(String uuid);

    List<Bill> findAllByCreatedBy(@Param("createdBy") String username);


}
