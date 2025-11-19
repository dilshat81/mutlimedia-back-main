package com.doranco.multimedia.repositories;

import com.doranco.multimedia.models.User;
import com.doranco.multimedia.wrapper.UserWrapper;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {

    List<UserWrapper> getAllUser();

    List<String> getAllAdmin();


    @Transactional
    @Modifying
    Integer updateStatus(@Param("status") String status, @Param("id") Integer id);

    @Query("select u from User u where u.email = :email")
    User findByEmail(@Param("email") String email);


}
