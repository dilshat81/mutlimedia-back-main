package com.doranco.multimedia.repositories;

import com.doranco.multimedia.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;


public interface CategoryDao extends JpaRepository<Category, Integer> {
    @Query("select c from Category c where c.id in (select p.category.id from Product p where p.status='true')")
    List<Category> getAllCategory();
}
