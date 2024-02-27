package com.altrh.multimedia.repositories;

import com.altrh.multimedia.models.User;
import com.altrh.multimedia.wrapper.UserWrapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {
    User findByEmailId(@Param("email") String email);

    List<UserWrapper> getAllUser();

    List<String> getAllAdmin();


    @Transactional
    @Modifying
    Integer updateStatus(@Param("status") String status, @Param("id") Integer id);

    User findByEmail(String email);


}
