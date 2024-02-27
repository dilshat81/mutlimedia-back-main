package com.altrh.multimedia.repositories;

import com.altrh.multimedia.models.Category;
import com.altrh.multimedia.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryDao extends JpaRepository<Category, Integer> {
    List<Category> getAllCategory();
}
